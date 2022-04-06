package com.pubnub.components.chat.ui.component.input

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.ButtonTheme
import com.pubnub.components.chat.ui.component.common.InputTheme
import com.pubnub.components.chat.ui.component.common.InputThemeDefaults
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class MessageInputTheme(
    modifier: Modifier,
    input: InputTheme,
    button: ButtonTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var input by mutableStateOf(input, structuralEqualityPolicy())
        internal set

    var button by mutableStateOf(button, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageInputTheme

        if (modifier != other.modifier) return false
        if (input != other.input) return false
        if (button != other.button) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modifier.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + button.hashCode()
        return result
    }
}

@Composable
fun MessageInputTheme(
    theme: MessageInputTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalMessageInputTheme provides theme) {
        content()
    }
}

val DefaultLocalMessageInputTheme @Composable get() = ThemeDefaults.messageInput(input = InputThemeDefaults.input())
val LocalMessageInputTheme = compositionLocalOf<MessageInputTheme> { throw MissingThemeException() }
