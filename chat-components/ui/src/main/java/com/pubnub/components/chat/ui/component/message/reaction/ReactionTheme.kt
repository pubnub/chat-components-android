package com.pubnub.components.chat.ui.component.message.reaction

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.ButtonTheme
import com.pubnub.components.chat.ui.component.common.ShapeTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class ReactionTheme(
    modifier: Modifier,
    selectedReaction: ButtonTheme,
    unselectedReaction: ButtonTheme,
    dialogShape: ShapeTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var selectedReaction by mutableStateOf(selectedReaction, structuralEqualityPolicy())
        internal set

    var unselectedReaction by mutableStateOf(unselectedReaction, structuralEqualityPolicy())
        internal set

    var dialogShape by mutableStateOf(dialogShape, structuralEqualityPolicy())
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
