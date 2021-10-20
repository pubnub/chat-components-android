package com.pubnub.components.chat.ui.component.channel

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
import com.pubnub.components.chat.ui.component.channel.renderer.ChannelRenderer
import com.pubnub.components.chat.ui.component.channel.renderer.DefaultChannelRenderer
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import kotlinx.coroutines.flow.Flow

@Composable
fun ChannelList(
    channels: List<ChannelUi.Data>,
    onSelected: (ChannelUi.Data) -> Unit = {},
    onLeave: ((ChannelUi.Data) -> Unit)? = null,
    header: @Composable (LazyItemScope) -> Unit = {},
    footer: @Composable (LazyItemScope) -> Unit = {},
    renderer: ChannelRenderer = DefaultChannelRenderer,
) {
    ChannelList(
        channels = mapOf(null to channels),
        onSelected = onSelected,
        onLeave = onLeave,
        header = header,
        footer = footer,
        renderer = renderer,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChannelList(
    channels: Map<String?, List<ChannelUi.Data>>,
    onSelected: (ChannelUi.Data) -> Unit = {},
    onAdd: (() -> Unit)? = null,
    onLeave: ((ChannelUi.Data) -> Unit)? = null,
    header: @Composable (LazyItemScope) -> Unit = {},
    footer: @Composable (LazyItemScope) -> Unit = {},
    renderer: ChannelRenderer = DefaultChannelRenderer,
) {
    checkNotNull(LocalPubNub.current)

    val theme = LocalChannelListTheme.current
    val context = LocalContext.current

    Box(modifier = theme.modifier) {
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.semantics {
                contentDescription = context.getString(R.string.channel_list)
            },
        ) {
            item {
                header(this)
            }
            channels.forEach { (header, list) ->
                renderer.renderSeparator(this, header, onAdd)
                items(list) { channel ->
                    renderer.Channel(
                        name = channel.name,
                        description = channel.description!!,
                        profileUrl = channel.profileUrl,
                        onClick = { onSelected(channel) },
                        onLeave = onLeave?.run { { onLeave(channel) } },
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
fun ChannelList(
    channels: Flow<PagingData<ChannelUi>>,
    onSelected: (ChannelUi.Data) -> Unit = {},
    onAdd: (() -> Unit)? = null,
    onLeave: ((ChannelUi.Data) -> Unit)? = null,
    header: @Composable (LazyItemScope) -> Unit = {},
    footer: @Composable (LazyItemScope) -> Unit = { scope: LazyItemScope -> },
    renderer: ChannelRenderer = DefaultChannelRenderer,
) {
    checkNotNull(LocalPubNub.current)

    val theme = LocalChannelListTheme.current
    val context = LocalContext.current

    // Scroll with paginated channels?
    val lazyItems = channels.collectAsLazyPagingItems()
    Box(modifier = theme.modifier) {
        val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.semantics {
                contentDescription = context.getString(R.string.channel_list)
            },
        ) {
            item {
                header(this)
            }
            items(lazyItems) { channel ->
                if (channel == null) {
                    renderer.Placeholder()
                    return@items
                }

                when (channel) {
                    is ChannelUi.Header -> {
                        renderer.Separator(title = channel.title, onClick = onAdd)
                    }
                    is ChannelUi.Data -> {
                        renderer.Channel(
                            name = channel.name,
                            description = channel.description!!,
                            profileUrl = channel.profileUrl,
                            onClick = { onSelected(channel) },
                            onLeave = onLeave?.run { { onLeave(channel) } },
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