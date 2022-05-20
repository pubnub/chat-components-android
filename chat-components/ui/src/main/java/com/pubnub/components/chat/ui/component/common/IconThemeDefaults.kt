package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

object IconThemeDefaults {
    @Composable
    fun icon(
        icon: ImageVector? = null,
        shape: Shape = CircleShape,
        tint: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
        modifier: Modifier = Modifier
            .size(40.dp)
            .padding(8.dp),
    ) = IconTheme(icon, shape, tint, modifier)

    @Composable
    fun menuIcon(
        shape: Shape = CircleShape,
        tint: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
        modifier: Modifier = Modifier.padding(8.dp),
    ) = IconTheme(null, shape, tint, modifier)
}
