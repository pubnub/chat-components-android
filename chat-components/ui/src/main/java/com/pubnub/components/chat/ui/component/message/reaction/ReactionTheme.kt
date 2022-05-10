package com.pubnub.components.chat.ui.component.message.reaction

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.ButtonTheme
import com.pubnub.components.chat.ui.component.common.FlowRowTheme
import com.pubnub.components.chat.ui.component.common.ModalBottomSheetLayoutTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class ReactionTheme(
    pickerModifier: Modifier,
    listFlowRow: FlowRowTheme,
    selectedReaction: ButtonTheme,
    notSelectedReaction: ButtonTheme,
) {
    var pickerModifier by mutableStateOf(pickerModifier, structuralEqualityPolicy())
        internal set

    var listFlowRow by mutableStateOf(listFlowRow, structuralEqualityPolicy())
        internal set

    var selectedReaction by mutableStateOf(selectedReaction, structuralEqualityPolicy())
        internal set

    var notSelectedReaction by mutableStateOf(notSelectedReaction, structuralEqualityPolicy())
        internal set
}


@Composable
fun ReactionTheme(
    theme: ReactionTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalReactionTheme provides theme) {
        content()
    }
}

val DefaultReactionTheme @Composable get() = ThemeDefaults.reaction()
val LocalReactionTheme = compositionLocalOf<ReactionTheme> { throw MissingThemeException() }
