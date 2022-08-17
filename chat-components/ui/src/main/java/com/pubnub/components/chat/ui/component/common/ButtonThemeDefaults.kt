package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ButtonThemeDefaults {
    @Composable
    fun button(
        elevation: ButtonElevation? = ButtonDefaults.elevation(),
        shape: Shape = MaterialTheme.shapes.small,
        border: BorderStroke? = null,
        colors: ButtonColors = ButtonDefaults.buttonColors(),
        contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
        text: TextTheme = TextThemeDefaults.text(),
        modifier: Modifier = Modifier,
    ) = ButtonTheme(elevation, shape, border, colors, contentPadding, text, modifier)

    @Composable
    fun selectedReaction() = button(
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colors.onSurface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f)),
        elevation = null,
        text = TextThemeDefaults.text(
            color = MaterialTheme.colors.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        ),
        contentPadding = PaddingValues(6.dp),
        modifier = Modifier
            .height(28.dp)
            .defaultMinSize(minWidth = 40.dp),
    )

    @Composable
    fun notSelectedReaction() = button(
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
            contentColor = MaterialTheme.colors.onSurface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f)),
        elevation = null,
        text = TextThemeDefaults.text(
            color = MaterialTheme.colors.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        ),
        contentPadding = PaddingValues(6.dp),
        modifier = Modifier
            .height(28.dp)
            .defaultMinSize(minWidth = 40.dp),
    )
}
