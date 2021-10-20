package com.pubnub.components.chat.sample.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pubnub.components.chat.sample.showToast
import com.pubnub.components.chat.ui.component.input.ImageInput
import com.pubnub.components.chat.ui.component.input.LinkInput
import com.pubnub.components.chat.ui.component.input.MessageInput
import com.pubnub.components.chat.ui.component.input.renderer.AnimatedTypingIndicatorRenderer

@Composable
fun MessageInputSampleView() {
    val context = LocalContext.current
    MessageInput(
        onSent = { message ->
            showToast(context, "Message sent: $message")
        },
        onChange = {}
    )
}

@Composable
fun MessageInputPubNubSampleView() {
    val context = LocalContext.current
    MessageInput(
        typingIndicator = true,
        typingIndicatorRenderer = AnimatedTypingIndicatorRenderer,
        onSuccess = { message, timetoken ->
            showToast(context, "Message sent: $message, timetoken: $timetoken")
        },
        onError = {
            showToast(context, "Sending error: $it")
        }
    )
}

@Composable
fun LinkInputPubNubSampleView() {
    val context = LocalContext.current
    LinkInput(
        onSuccess = { message, timetoken ->
            showToast(context, "Message sent: $message, timetoken: $timetoken")
        },
        onError = {
            showToast(context, "Sending error: $it")
        }
    )
}

@Composable
fun ImageInputPubNubSampleView() {
    val context = LocalContext.current
    ImageInput(
        onSuccess = { message, timetoken ->
            showToast(context, "Message sent: $message, timetoken: $timetoken")
        },
        onError = {
            showToast(context, "Sending error: $it")
        }
    )
}
