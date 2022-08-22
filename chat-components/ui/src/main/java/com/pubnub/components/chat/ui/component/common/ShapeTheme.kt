package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

class ShapeTheme(
    shape: Shape,
    tint: Color,
    padding: PaddingValues,
    modifier: Modifier,
) {

    var shape by mutableStateOf(shape, structuralEqualityPolicy())
        internal set

    var tint by mutableStateOf(tint, structuralEqualityPolicy())
        internal set

    var padding by mutableStateOf(padding, structuralEqualityPolicy())
        internal set

    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShapeTheme

        if (shape != other.shape) return false
        if (tint != other.tint) return false
        if (padding != other.padding) return false
        if (modifier != other.modifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shape.hashCode()
        result = 31 * result + tint.hashCode()
        result = 31 * result + padding.hashCode()
        result = 31 * result + modifier.hashCode()
        return result
    }
}
