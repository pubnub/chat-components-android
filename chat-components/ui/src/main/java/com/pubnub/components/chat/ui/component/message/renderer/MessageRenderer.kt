package com.pubnub.components.chat.ui.component.message.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.pubnub.components.chat.ui.component.message.Attachment
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.components.chat.ui.component.message.reaction.SelectedReaction
import com.pubnub.components.chat.ui.component.message.reaction.renderer.ReactionPickerRenderer
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken

interface MessageRenderer {
    @Composable
    fun Message(
        messageId: MessageId,
        currentUserId: UserId,
        userId: UserId,
        profileUrl: String,
        online: Boolean?,
        title: String,
        message: AnnotatedString?,
        attachments: List<Attachment>?,
        timetoken: Timetoken,
        navigateToProfile: (UserId) -> Unit,
        reactions: List<ReactionUi>,
        onReaction: ((SelectedReaction) -> Unit)?,
        reactionPickerRenderer: ReactionPickerRenderer,
    )

    @Composable
    fun Separator(text: String)

    @Composable
    fun Placeholder() {
    }
}