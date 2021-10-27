package com.pubnub.components.chat.ui.component.common

import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Shape

class InputTheme(
    shape: Shape,
    colors: TextFieldColors,
) {

    var shape by mutableStateOf(shape, structuralEqualityPolicy())
        internal set

    var colors by mutableStateOf(colors, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InputTheme

        if (shape != other.shape) return false
        if (colors != other.colors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shape.hashCode()
        result = 31 * result + colors.hashCode()
        return result
    }
}
