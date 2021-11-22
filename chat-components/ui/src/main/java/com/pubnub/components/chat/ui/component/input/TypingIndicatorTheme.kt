package com.pubnub.components.chat.ui.component.input

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.IconTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class TypingIndicatorTheme(
    modifier: Modifier,
    icon: IconTheme,
    text: TextTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var icon by mutableStateOf(icon, structuralEqualityPolicy())
        internal set

    var text by mutableStateOf(text, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypingIndicatorTheme

        if (modifier != other.modifier) return false
        if (icon != other.icon) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modifier.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + text.hashCode()
        return result
    }
}

@Composable
fun TypingIndicatorTheme(
    theme: TypingIndicatorTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalTypingIndicatorTheme provides theme) {
        content()
    }
}

val DefaultTypingIndicatorTheme @Composable get() = ThemeDefaults.typingIndicator()
val LocalTypingIndicatorTheme =
    compositionLocalOf<TypingIndicatorTheme> { throw MissingThemeException() }
