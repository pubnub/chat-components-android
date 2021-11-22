package com.pubnub.components.chat.ui.component.member

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pubnub.components.chat.ui.component.common.IconTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class MemberListTheme(
    modifier: Modifier,
    name: TextTheme,
    description: TextTheme,
    image: Modifier,
    icon: IconTheme,
    header: TextTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var name by mutableStateOf(name, structuralEqualityPolicy())
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
fun MemberListTheme(
    theme: MemberListTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalMemberListTheme provides theme) {
        content()
    }
}

val DefaultMemberListTheme @Composable get() = ThemeDefaults.memberList()
val LocalMemberListTheme = compositionLocalOf<MemberListTheme> { throw MissingThemeException() }
