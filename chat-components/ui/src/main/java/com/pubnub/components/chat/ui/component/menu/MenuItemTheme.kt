package com.pubnub.components.chat.ui.component.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pubnub.components.chat.ui.component.common.*
import com.pubnub.components.chat.ui.component.message.ProfileImageTheme
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class MenuItemTheme(
    modifier: Modifier = Modifier,
    text: TextTheme,
    icon: IconTheme,
    horizontalArrangement: Arrangement.Horizontal,
    verticalAlignment: Alignment.Vertical,
) {

    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var icon by mutableStateOf(icon, structuralEqualityPolicy())
        internal set

    var text by mutableStateOf(text, structuralEqualityPolicy())
        internal set

    var horizontalArrangement by mutableStateOf(horizontalArrangement, structuralEqualityPolicy())
        internal set

    var verticalAlignment by mutableStateOf(verticalAlignment, structuralEqualityPolicy())
        internal set

}


@Composable
fun MenuItemTheme(
    theme: MenuItemTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalMenuItemTheme provides theme) {
        content()
    }
}

val DefaultMenuItemTheme @Composable get() = ThemeDefaults.menuItem()
val LocalMenuItemTheme = compositionLocalOf<MenuItemTheme> { throw MissingThemeException() }

