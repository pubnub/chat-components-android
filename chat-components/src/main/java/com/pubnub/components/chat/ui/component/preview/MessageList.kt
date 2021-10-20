package com.pubnub.components.chat.ui.component.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pubnub.components.chat.provider.PubNubPreview
import com.pubnub.components.chat.ui.component.message.messageFormatter
import com.pubnub.components.chat.ui.component.message.renderer.GroupMessageRenderer.GroupChatMessage
import com.pubnub.components.chat.ui.component.message.renderer.GroupMessageRenderer.GroupChatMessagePlaceholder

@Preview
@Composable
private fun GroupChatMessageOnlinePreview() {
    PubNubPreview {
        GroupChatMessage(
            currentUserId = "userId2",
            userId = "userId",
            profileUrl = "url",
            online = true,
            title = "Lorem ipsum",
            message = messageFormatter(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            attachments = null,
            timetoken = 0L,
            navigateToProfile = { },
        )
    }
}

@Preview
@Composable
private fun GroupChatMessageOfflinePreview() {
    PubNubPreview {
        GroupChatMessage(
            currentUserId = "userId2",
            userId = "userId",
            profileUrl = "url",
            online = false,
            title = "Lorem ipsum",
            message = messageFormatter(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            attachments = null,
            timetoken = 0L,
            navigateToProfile = { },
        )
    }
}

@Preview
@Composable
private fun GroupChatMessagePlaceholderPreview() {
    PubNubPreview {
        GroupChatMessagePlaceholder()
    }
}