package com.pubnub.components.chat.ui.component.message.reaction.renderer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.framework.data.UserId

@OptIn(ExperimentalAnimationApi::class)
interface ReactionPickerRenderer {

    @Composable
    fun Dialog(
        scope: AnimatedVisibilityScope?,
        onSelected: (String) -> Unit,
        onClose: () -> Unit,
    )

    @Composable
    fun Selector(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (String) -> Unit,
        onAdd: () -> Unit,
    )
}