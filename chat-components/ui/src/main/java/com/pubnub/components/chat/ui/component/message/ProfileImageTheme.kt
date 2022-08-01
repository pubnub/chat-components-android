package com.pubnub.components.chat.ui.component.message

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pubnub.components.chat.ui.component.common.ThemeDefaults
import com.pubnub.components.chat.ui.component.provider.MissingThemeException

class ProfileImageTheme(
    modifier: Modifier,
    indicatorTheme: IndicatorTheme,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var indicatorTheme by mutableStateOf(indicatorTheme, structuralEqualityPolicy())
        internal set
}

class IndicatorTheme(
    modifier: Modifier,
    align: Alignment,
    activeColor: Color,
    inactiveColor: Color,
    borderStroke: BorderStroke,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var align by mutableStateOf(align, structuralEqualityPolicy())
        internal set

    var activeColor by mutableStateOf(activeColor, structuralEqualityPolicy())
        internal set

    var inactiveColor by mutableStateOf(inactiveColor, structuralEqualityPolicy())
        internal set

    var borderStroke by mutableStateOf(borderStroke, structuralEqualityPolicy())
        internal set
}

@Composable
fun ProfileImageTheme(
    theme: ProfileImageTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalProfileImageTheme provides theme) {
        content()
    }
}

val DefaultProfileImageTheme @Composable get() = ThemeDefaults.profileImage()
val LocalProfileImageTheme = compositionLocalOf<ProfileImageTheme> { throw MissingThemeException() }

@Composable
fun IndicatorTheme(
    theme: IndicatorTheme,
    content: @Composable() () -> Unit,
) {
    CompositionLocalProvider(LocalIndicatorTheme provides theme) {
        content()
    }
}

val DefaultIndicatorTheme @Composable get() = ThemeDefaults.indicator()
val LocalIndicatorTheme = compositionLocalOf<IndicatorTheme> { throw MissingThemeException() }
