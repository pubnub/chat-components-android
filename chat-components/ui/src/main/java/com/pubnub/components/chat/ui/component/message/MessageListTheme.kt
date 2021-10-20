package com.pubnub.components.chat.ui.component.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class MessageListTheme(
    modifier: Modifier,
    arrangement: Arrangement.Vertical,
    message: MessageTheme,
    messageOwn: MessageTheme = message,
    separator: TextTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var arrangement by mutableStateOf(arrangement, structuralEqualityPolicy())
        internal set

    var message by mutableStateOf(message, structuralEqualityPolicy())
        internal set

    var messageOwn by mutableStateOf(messageOwn, structuralEqualityPolicy())
        internal set

    var separator by mutableStateOf(separator, structuralEqualityPolicy())
        internal set
}

@Composable
fun MessageListTheme(
    theme: MessageListTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalMessageListTheme provides theme) {
        content()
    }
}

val DefaultMessageListTheme @Composable get() = ThemeDefaults.messageList()
val LocalMessageListTheme = compositionLocalOf<MessageListTheme> { throw MissingThemeException() }
