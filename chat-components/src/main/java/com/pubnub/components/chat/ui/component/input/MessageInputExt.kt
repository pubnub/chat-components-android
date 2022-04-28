package com.pubnub.components.chat.ui.component.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.pubnub.components.R
import com.pubnub.components.chat.network.data.NetworkMessage
import com.pubnub.components.chat.ui.component.input.renderer.DefaultTypingIndicatorRenderer
import com.pubnub.components.chat.ui.component.input.renderer.TypingIndicatorRenderer
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.framework.service.LocalTypingService
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun LinkInput(
    placeholder: String = stringResource(id = R.string.type_link),
    initialText: String = "",
    onChange: (String) -> Unit = {},
    onSuccess: (String, Timetoken) -> Unit = { _, _ -> },
    onError: (Exception) -> Unit = {},
    coroutineScope: CoroutineScope = GlobalScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    checkNotNull(LocalPubNub.current)
    checkNotNull(LocalChannel.current)

    val channel = LocalChannel.current

    val viewModel: MessageInputViewModel = MessageInputViewModel.default()

    // region actions
    val sendAction: (String) -> Unit = { message ->
        coroutineScope.launch(dispatcher) {
            // send message
            viewModel.send(
                channel,
                "",
                NetworkMessage.Type.DEFAULT,
                listOf(
                    NetworkMessage.Attachment.Link(message)
                ),
                onSuccess,
                onError
            )
        }
    }

    // endregion
    MessageInput(
        placeholder = placeholder,
        initialText = initialText,
        onSent = sendAction, onChange
    )
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ImageInput(
    placeholder: String = stringResource(id = R.string.type_image),
    initialText: String = "",
    onChange: (String) -> Unit = {},
    onSuccess: (String, Timetoken) -> Unit = { _, _ -> },
    onError: (Exception) -> Unit = {},
    coroutineScope: CoroutineScope = GlobalScope,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    checkNotNull(LocalPubNub.current)
    checkNotNull(LocalChannel.current)

    val channel = LocalChannel.current

    val viewModel: MessageInputViewModel = MessageInputViewModel.default()

    // region actions
    val sendAction: (String) -> Unit = { message ->
        coroutineScope.launch(dispatcher) {
            // send message
            viewModel.send(
                channel,
                "",
                NetworkMessage.Type.DEFAULT,
                listOf(NetworkMessage.Attachment.Image(message)),
                onSuccess,
                onError
            )
        }
    }

    // endregion
    MessageInput(
        placeholder = placeholder,
        initialText = initialText,
        onSent = sendAction, onChange
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MessageInput(
    initialText: String = "",
    placeholder: String = stringResource(id = R.string.type_message),
    typingIndicator: Boolean = false,
    typingIndicatorRenderer: TypingIndicatorRenderer = DefaultTypingIndicatorRenderer,
    onSuccess: (String, Timetoken) -> Unit = { _, _ -> },
    onError: (Exception) -> Unit = {},
) {
    checkNotNull(LocalChannel.current)

    val channel = LocalChannel.current

    val viewModel: MessageInputViewModel =
        MessageInputViewModel.default(typingService = if (typingIndicator) LocalTypingService.current else null)

    // region actions
    val typingAction: (String) -> Unit = { message ->
        if (typingIndicator)
            viewModel.setTyping(channel, message.isNotEmpty())
    }

    val sendAction: (String) -> Unit = { message ->
        // Hide typing indicator
        typingAction("")

        // Send message
        viewModel.send(channel, message, NetworkMessage.Type.DEFAULT, null, onSuccess, onError)
    }
    // endregion

    Column {
        if (typingIndicator) {
            val typingService = LocalTypingService.current
            DisposableEffect(channel) {
                typingService.bind(channel)
                onDispose { typingService.unbind() }
            }

            val typing by typingService.getTyping(channel)
                .collectAsState(initial = emptyList())
            AnimatedVisibility(
                visible = typing.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                typingIndicatorRenderer.TypingIndicator(data = typing)
            }
        }
        MessageInput(
            placeholder = placeholder,
            initialText = initialText,
            onSent = sendAction,
            onChange = typingAction,
        )
    }
}
