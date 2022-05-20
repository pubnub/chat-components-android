package com.pubnub.components.chat.ui.component.message

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.ShapeTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class MessageTheme(
    modifier: Modifier,
    title: TextTheme,
    date: TextTheme,
    text: TextTheme,
    profileImage: ProfileImageTheme,
    shape: ShapeTheme,
    verticalAlignment: Alignment.Vertical,

    ) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var title by mutableStateOf(title, structuralEqualityPolicy())
        internal set

    var date by mutableStateOf(date, structuralEqualityPolicy())
        internal set

    var text by mutableStateOf(text, structuralEqualityPolicy())
        internal set

    var profileImage by mutableStateOf(profileImage, structuralEqualityPolicy())
        internal set

    var shape by mutableStateOf(shape, structuralEqualityPolicy())
        internal set

    var verticalAlignment by mutableStateOf(verticalAlignment, structuralEqualityPolicy())
        internal set

}


@Composable
fun MessageTheme(
    theme: MessageTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalMessageTheme provides theme) {
        content()
    }
}

val DefaultLocalMessageTheme @Composable get() = ThemeDefaults.message()
val LocalMessageTheme = compositionLocalOf<MessageTheme> { throw MissingThemeException() }
