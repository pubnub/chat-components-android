package com.pubnub.components.chat.ui.component.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector

class IconTheme(
    icon: ImageVector?,
    shape: Shape,
    tint: Color,
    modifier: Modifier,
) {
    var icon by mutableStateOf(icon, structuralEqualityPolicy())
        internal set

    var shape by mutableStateOf(shape, structuralEqualityPolicy())
        internal set

    var tint by mutableStateOf(tint, structuralEqualityPolicy())
        internal set

    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconTheme

        if (icon != other.icon) return false
        if (shape != other.shape) return false
        if (tint != other.tint) return false
        if (modifier != other.modifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + shape.hashCode()
        result = 31 * result + tint.hashCode()
        result = 31 * result + modifier.hashCode()
        return result
    }
}
