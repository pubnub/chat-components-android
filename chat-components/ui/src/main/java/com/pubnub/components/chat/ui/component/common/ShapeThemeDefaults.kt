package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object ShapeThemeDefaults {
    @Composable
    fun shape(
        modifier: Modifier = Modifier,
        color: Color = TextFieldDefaults.textFieldColors().backgroundColor(true).value,
        padding: PaddingValues = PaddingValues(0.dp),
        shape: Shape,
    ) = ShapeTheme(shape, color, padding, modifier)

    @Composable
    fun dialogShape(
        modifier:Modifier = Modifier.fillMaxWidth().padding(8.dp),
        color: Color = MaterialTheme.colors.background,
        shape: Shape = MaterialTheme.shapes.large,
    ) = shape(
        modifier = modifier,
        color = color,
        shape = shape,
    ),
    @Composable
    fun linkPreview(
        shape: Shape = RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp),
        color: Color = MaterialTheme.colors.surface,
        padding: PaddingValues = PaddingValues(8.dp),
    ) = shape(shape = shape, padding = padding, color = color)

    @Composable
    fun linkPreviewImage(
        modifier: Modifier = Modifier
            .defaultMinSize(minHeight = 100.dp)
            .fillMaxWidth(),
        shape: Shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 0.dp),
        color: Color = MaterialTheme.colors.surface,
        padding: PaddingValues = PaddingValues(0.dp),
    ) = shape(modifier = modifier, shape = shape, padding = padding, color = color)

    @Composable
    fun messageBackground(
        shape: Shape = RoundedCornerShape(0.dp),
        color: Color = MaterialTheme.colors.background,
        padding: PaddingValues = PaddingValues(0.dp, 6.dp, 0.dp, 0.dp),
    ) = shape(shape = shape, padding = padding, color = color)
}
