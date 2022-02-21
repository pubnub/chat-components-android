package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

object ButtonThemeDefaults {
    @Composable
    fun button(
        elevation: ButtonElevation? = ButtonDefaults.elevation(),
        shape: Shape = MaterialTheme.shapes.small,
        border: BorderStroke? = null,
        colors: ButtonColors = ButtonDefaults.buttonColors(),
        contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
        text: TextTheme = TextThemeDefaults.text(),
        modifier: Modifier = Modifier
    ) = ButtonTheme(elevation, shape, border, colors, contentPadding, text, modifier)
}
