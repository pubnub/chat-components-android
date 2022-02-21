package com.pubnub.components.chat.ui.component.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

object InputThemeDefaults {
    @Composable
    fun input(
        shape: Shape = MaterialTheme.shapes.medium,
        colors: TextFieldColors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background), // Workaround for text color not changes after theme switch
        ),
    ) = InputTheme(shape, colors)
}
