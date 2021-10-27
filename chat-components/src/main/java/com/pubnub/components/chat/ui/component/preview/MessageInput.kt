package com.pubnub.components.chat.ui.component.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pubnub.components.chat.provider.PubNubPreview
import com.pubnub.components.chat.ui.component.input.MessageInput

@Preview
@Composable
private fun MessageInputPreviewWithHint() {
    PubNubPreview {
        MessageInput()
    }
}

@Preview
@Composable
private fun MessageInputPreview() {
    PubNubPreview {
        MessageInput(initialText = "Hello World!")
    }
}
