package com.pubnub.components.chat.ui.component.member

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.member.renderer.DefaultMemberRenderer
import com.pubnub.components.chat.ui.component.member.renderer.MemberRenderer
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.framework.util.hash
import kotlinx.coroutines.flow.Flow

@Composable
fun MemberList(
    members: List<MemberUi.Data>,
    presence: Presence? = null,
    onSelected: (MemberUi.Data) -> Unit = {},
    header: @Composable (LazyItemScope) -> Unit = {},
    footer: @Composable (LazyItemScope) -> Unit = {},
    renderer: MemberRenderer = DefaultMemberRenderer,
) {
    MemberList(
        members = mapOf(null to members),
        presence = presence,
        onSelected = onSelected,
        header = header,
        footer = footer,
        renderer = renderer,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemberList(
    members: Map<String?, List<MemberUi.Data>>,
    presence: Presence? = null,
    onSelected: (MemberUi.Data) -> Unit = {},
    header: @Composable (LazyItemScope) -> Unit = {},
    footer: @Composable (LazyItemScope) -> Unit = {},
    renderer: MemberRenderer = DefaultMemberRenderer,
) {
    checkNotNull(LocalPubNub.current)

    val theme = LocalMemberListTheme.current
    val context = LocalContext.current

    Box(modifier = theme.modifier) {
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.semantics {
                contentDescription = context.getString(R.string.member_list)
            },
        ) {
            item {
                header(this)
            }
            members.forEach { (header, list) ->
                renderer.renderSeparator(this, header)

                items(list) { member ->
                    val online = presence?.get(member.id)?.value
                    renderer.Member(
                        name = member.name,
                        description = member.description,
                        profileUrl = member.profileUrl ?: getRandomProfileUrl(member.id),
                        online = online,
                        onClick = { onSelected(member) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            item {
                footer(this)
            }
        }
    }
}

@Composable
fun MemberList(
    members: Flow<PagingData<MemberUi>>,
    presence: Presence? = null,
    onSelected: (MemberUi.Data) -> Unit = {},
    header: @Composable (LazyItemScope) -> Unit = {},
    footer: @Composable (LazyItemScope) -> Unit = {},
    renderer: MemberRenderer = DefaultMemberRenderer,
) {

    val theme = LocalMemberListTheme.current
    val context = LocalContext.current

    val lazyItems = members.collectAsLazyPagingItems()
    Box(modifier = theme.modifier) {
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.semantics {
                contentDescription = context.getString(R.string.member_list)
            },
        ) {
            item {
                header(this)
            }
            items(lazyItems) { member ->
                if (member == null) {
                    renderer.Placeholder()
                    return@items
                }

                when (member) {
                    is MemberUi.Separator -> {
                        renderer.Separator(title = member.text)
                    }
                    is MemberUi.Data -> {
                        renderer.Member(
                            name = member.name,
                            description = member.description,
                            profileUrl = member.profileUrl ?: getRandomProfileUrl(member.id),
                            online = presence?.get(member.id)?.value,//member.online.value,
                            onClick = { onSelected(member) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item {
                footer(this)
            }
        }
    }
}


fun getRandomProfileUrl(id: String) =
    "https://www.gravatar.com/avatar/${hash(id)}?s=256&d=identicon"

