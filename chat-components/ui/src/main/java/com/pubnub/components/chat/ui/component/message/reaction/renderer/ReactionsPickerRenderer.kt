package com.pubnub.components.chat.ui.component.message.reaction.renderer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.pubnub.components.chat.ui.component.message.reaction.Emoji
import com.pubnub.components.chat.ui.component.message.reaction.Reaction
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.framework.data.UserId

@OptIn(ExperimentalAnimationApi::class)
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
