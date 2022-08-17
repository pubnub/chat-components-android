package com.pubnub.components.chat.ui.component.member

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.*
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
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.framework.util.hash
import kotlinx.coroutines.flow.Flow

@Composable
fun MemberList(
    members: List<MemberUi>,
    presence: Presence? = null,
    onSelected: (MemberUi.Data) -> Unit = {},
    useStickyHeader: Boolean = false,
    headerContent: @Composable (LazyItemScope) -> Unit = {},
    footerContent: @Composable (LazyItemScope) -> Unit = {},
    itemContent: @Composable LazyListScope.(MemberUi?) -> Unit = { member ->
        MemberListContent(member, useStickyHeader, presence, onSelected)
    },
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
                headerContent(this)
            }
            items(members) { member ->
                this@LazyColumn.itemContent(member)
            }
            item {
                footerContent(this)
            }
        }
    }
}

@Composable
fun MemberList(
    members: Flow<PagingData<MemberUi>>,
    presence: Presence? = null,
    onSelected: (MemberUi.Data) -> Unit = {},
    useStickyHeader: Boolean = false,
    headerContent: @Composable (LazyItemScope) -> Unit = {},
    footerContent: @Composable (LazyItemScope) -> Unit = {},
    itemContent: @Composable LazyListScope.(MemberUi?) -> Unit = { member ->
        MemberListContent(member, useStickyHeader, presence, onSelected)
    },
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
                headerContent(this)
            }
            items(lazyItems) { member ->
                this@LazyColumn.itemContent(member)
            }
            item {
                footerContent(this)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyListScope.MemberListContent(
    member: MemberUi?,
    useStickyHeader: Boolean,
    presence: Presence?,
    onSelected: (MemberUi.Data) -> Unit,
) {
    when (member) {
        null -> {
            DefaultMemberRenderer.Placeholder()
        }
        is MemberUi.Separator -> {
            if (useStickyHeader)
                stickyHeader(member.text) {
                    DefaultMemberRenderer.Separator(
                        title = member.text,
                    )
                }
            else
                DefaultMemberRenderer.Separator(
                    title = member.text,
                )
        }
        is MemberUi.Data -> {
            val online = presence?.get(member.id)?.value
            DefaultMemberRenderer.Member(
                name = member.name ?: member.id,
                description = member.description,
                profileUrl = member.profileUrl ?: getRandomProfileUrl(member.id),
                online = online,
                onClick = { onSelected(member) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

fun getRandomProfileUrl(id: String) =
    "https://www.gravatar.com/avatar/${hash(id)}?s=256&d=identicon"

