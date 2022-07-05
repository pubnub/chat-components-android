package com.pubnub.components.chat.ui.component.message

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.member.getRandomProfileUrl
import com.pubnub.components.chat.ui.component.menu.React
import com.pubnub.components.chat.ui.component.message.reaction.renderer.DefaultReactionsPickerRenderer
import com.pubnub.components.chat.ui.component.message.renderer.GroupMessageRenderer
import com.pubnub.components.chat.ui.component.message.renderer.MessageRenderer
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalUser
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageList(
    messages: Flow<PagingData<MessageUi>>,
    modifier: Modifier = Modifier,
    presence: Presence? = null,
    onMessageSelected: ((MessageUi.Data) -> Unit)? = null,
    onReactionSelected: ((React) -> Unit)? = null,
    useStickyHeader: Boolean = false,
    headerContent: @Composable LazyItemScope.() -> Unit = {},
    footerContent: @Composable LazyItemScope.() -> Unit = {},
    itemContent: @Composable LazyListScope.(MessageUi?, UserId) -> Unit = { message, currentUser ->
        MessageListContent(
            message,
            currentUser,
            GroupMessageRenderer,
            DefaultReactionsPickerRenderer,
            useStickyHeader,
            presence,
            onMessageSelected,
            onReactionSelected
        )
    },
) {

    val theme = LocalMessageListTheme.current
    val context = LocalContext.current
    val currentUser = LocalUser.current

    val lazyMessages: LazyPagingItems<MessageUi> = messages.collectAsLazyPagingItems()

    Box(modifier = modifier) {
        val lazyListState =
            rememberLazyListState(initialFirstVisibleItemIndex = lazyMessages.itemCount)

        LazyColumn(
            state = lazyListState,
            reverseLayout = true,
            verticalArrangement = theme.arrangement,
            modifier = theme.modifier.semantics {
                contentDescription = context.getString(R.string.message_list)
            }) {
            item {
                headerContent()
            }
            items(lazyMessages) { message ->
                this@LazyColumn.itemContent(message, currentUser)
            }
            item {
                footerContent()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyListScope.MessageListContent(
    message: MessageUi?,
    currentUser: UserId,
    messageRenderer: MessageRenderer = GroupMessageRenderer,
    reactionsPickerRenderer: DefaultReactionsPickerRenderer,
    useStickyHeader: Boolean,
    presence: Presence?,
    onMessageSelected: ((MessageUi.Data) -> Unit)?,
    onReactionSelected: ((React) -> Unit)?,
) {
    when (message) {
        null -> {
            messageRenderer.Placeholder()
        }
        is MessageUi.Separator -> {
            if (useStickyHeader)
                stickyHeader(message.text) {
                    messageRenderer.Separator(text = message.text)
                }
            else
                messageRenderer.Separator(text = message.text)

        }
        is MessageUi.Data -> {
            val styledMessage = messageFormatter(text = message.text)

            messageRenderer.Message(
                messageId = message.uuid,
                currentUserId = currentUser,
                userId = message.publisher.id,
                profileUrl = message.publisher.profileUrl
                    ?: getRandomProfileUrl(message.publisher.id),
                online = presence?.get(message.publisher.id)?.value,
                title = message.publisher.name ?: message.publisher.id,
                message = styledMessage,
                timetoken = message.timetoken,
                reactions = message.reactions,
                onMessageSelected = onMessageSelected?.let { { it.invoke(message) } },
                onReactionSelected = onReactionSelected?.let {
                    { reaction ->
                        it.invoke(React(reaction, message))
                    }
                },
                reactionsPickerRenderer = reactionsPickerRenderer,
            )
        }
    }
}
