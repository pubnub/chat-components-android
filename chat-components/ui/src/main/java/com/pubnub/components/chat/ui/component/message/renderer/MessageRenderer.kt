package com.pubnub.components.chat.ui.component.message.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.pubnub.components.chat.ui.component.message.reaction.PickedReaction
import com.pubnub.components.chat.ui.component.message.reaction.Reaction
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.components.chat.ui.component.message.reaction.renderer.ReactionsRenderer
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
        timetoken: Timetoken,
        reactions: List<ReactionUi>,
        onMessageSelected: (() -> Unit)?,
        onReactionSelected: ((Reaction) -> Unit)?,
        reactionsPickerRenderer: ReactionsRenderer,
    )

    @Composable
    fun Separator(text: String)

    @Composable
    fun Placeholder() {
    }
}