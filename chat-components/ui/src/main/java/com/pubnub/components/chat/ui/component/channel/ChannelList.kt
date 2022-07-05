package com.pubnub.components.chat.ui.component.channel

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
import com.pubnub.components.chat.ui.component.channel.renderer.DefaultChannelRenderer
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import kotlinx.coroutines.flow.Flow

@Composable
fun ChannelList(
    channels: List<ChannelUi>,
    onSelected: (ChannelUi.Data) -> Unit = {},
    onAdd: (() -> Unit)? = null,
    onLeave: ((ChannelUi.Data) -> Unit)? = null,
    useStickyHeader: Boolean = false,
    headerContent: @Composable (LazyItemScope) -> Unit = {},
    footerContent: @Composable (LazyItemScope) -> Unit = {},
    itemContent: @Composable LazyListScope.(ChannelUi?) -> Unit = { channel ->
        ChannelListContent(channel, useStickyHeader, onSelected, onAdd, onLeave)
    },
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
                headerContent(this)
            }

            items(channels) { channel ->
                this@LazyColumn.itemContent(channel)
            }

            item {
                footerContent(this)
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
    useStickyHeader: Boolean = false,
    headerContent: @Composable (LazyItemScope) -> Unit = {},
    footerContent: @Composable (LazyItemScope) -> Unit = { _: LazyItemScope -> },
    itemContent: @Composable LazyListScope.(ChannelUi?) -> Unit = { channel ->
        ChannelListContent(channel, useStickyHeader, onSelected, onAdd, onLeave)
    },
) {
    checkNotNull(LocalPubNub.current)

    val theme = LocalChannelListTheme.current
    val context = LocalContext.current

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
                headerContent(this)
            }
            items(lazyItems) { channel ->
                this@LazyColumn.itemContent(channel)
            }
            item {
                footerContent(this)
            }
        }
    }
}

@Composable
fun LazyListScope.ChannelListContent(
    channel: ChannelUi?,
    useStickyHeader: Boolean = false,
    onSelected: (ChannelUi.Data) -> Unit,
    onAdd: (() -> Unit)?,
    onLeave: ((ChannelUi.Data) -> Unit)?,
){
    when (channel) {
        null -> {
            DefaultChannelRenderer.Placeholder()
        }
        is ChannelUi.Header -> {
            if(useStickyHeader)
                DefaultChannelRenderer.Separator(
                    title = channel.title,
                    onClick = onAdd
                )
            else
                DefaultChannelRenderer.renderSeparator(
                    scope = this,
                    title = channel.title,
                    onClick = onAdd
                )
        }
        is ChannelUi.Data -> {
            DefaultChannelRenderer.Channel(
                name = channel.name,
                description = channel.description,
                profileUrl = channel.profileUrl,
                onClick = { onSelected(channel) },
                onLeave = onLeave?.run { { onLeave(channel) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
