package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonElevation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

class ButtonTheme(
    elevation: ButtonElevation?,
    shape: Shape,
    border: BorderStroke?,
    colors: ButtonColors,
    contentPadding: PaddingValues,
    text: TextTheme,
    modifier: Modifier,
) {
    var elevation by mutableStateOf(elevation, structuralEqualityPolicy())
        internal set

    var shape by mutableStateOf(shape, structuralEqualityPolicy())
        internal set

    var border by mutableStateOf(border, structuralEqualityPolicy())
        internal set

    var colors by mutableStateOf(colors, structuralEqualityPolicy())
        internal set

    var contentPadding by mutableStateOf(contentPadding, structuralEqualityPolicy())
        internal set

    var text by mutableStateOf(text, structuralEqualityPolicy())
        internal set

    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ButtonTheme

        if (shape != other.shape) return false
        if (border != other.border) return false
        if (colors != other.colors) return false
        if (contentPadding != other.contentPadding) return false
        if (text != other.text) return false
        if (modifier != other.modifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shape.hashCode()
        result = 31 * result + (border?.hashCode() ?: 0)
        result = 31 * result + colors.hashCode()
        result = 31 * result + contentPadding.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + modifier.hashCode()
        return result
    }
}
