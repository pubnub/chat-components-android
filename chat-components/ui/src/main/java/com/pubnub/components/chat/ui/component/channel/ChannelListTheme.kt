package com.pubnub.components.chat.ui.component.channel

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.IconTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class ChannelListTheme(
    modifier: Modifier,
    title: TextTheme,
    description: TextTheme,
    image: Modifier,
    icon: IconTheme,
    header: TextTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var title by mutableStateOf(title, structuralEqualityPolicy())
        internal set

    var description by mutableStateOf(description, structuralEqualityPolicy())
        internal set

    var image by mutableStateOf(image, structuralEqualityPolicy())
        internal set

    var icon by mutableStateOf(icon, structuralEqualityPolicy())
        internal set

    var header by mutableStateOf(header, structuralEqualityPolicy())
        internal set
}

@Composable
fun ChannelListTheme(
    theme: ChannelListTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalChannelListTheme provides theme) {
        content()
    }
}

val DefaultChannelListTheme @Composable get() = ThemeDefaults.channelList()
val LocalChannelListTheme = compositionLocalOf<ChannelListTheme> { throw MissingThemeException() }
