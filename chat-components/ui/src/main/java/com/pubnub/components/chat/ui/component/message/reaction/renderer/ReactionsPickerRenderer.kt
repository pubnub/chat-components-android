package com.pubnub.components.chat.ui.component.message.reaction.renderer

import androidx.compose.runtime.Composable
import com.pubnub.components.chat.ui.component.message.reaction.Reaction
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.framework.data.UserId

interface ReactionsRenderer {

    @Composable
    fun Picker(
        onSelected: (Reaction) -> Unit,
    )

    @Composable
    fun PickedList(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (Reaction) -> Unit,
    )
}
