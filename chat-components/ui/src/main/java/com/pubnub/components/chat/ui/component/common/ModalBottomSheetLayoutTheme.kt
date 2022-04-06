package com.pubnub.components.chat.ui.component.common

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp


@OptIn(ExperimentalMaterialApi::class)
class ModalBottomSheetLayoutTheme(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    sheetShape: Shape,
    sheetElevation: Dp,
    sheetBackgroundColor: Color,
    sheetContentColor: Color,
    scrimColor: Color,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var sheetState by mutableStateOf(sheetState, structuralEqualityPolicy())
        internal set

    var sheetShape by mutableStateOf(sheetShape, structuralEqualityPolicy())
        internal set

    var sheetElevation by mutableStateOf(sheetElevation, structuralEqualityPolicy())
        internal set

    var sheetBackgroundColor by mutableStateOf(sheetBackgroundColor, structuralEqualityPolicy())
        internal set

    var sheetContentColor by mutableStateOf(sheetContentColor, structuralEqualityPolicy())
        internal set

    var scrimColor by mutableStateOf(scrimColor, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModalBottomSheetLayoutTheme

        if (modifier != other.modifier) return false
        if (sheetState != other.sheetState) return false
        if (sheetShape != other.sheetShape) return false
        if (sheetElevation != other.sheetElevation) return false
        if (sheetBackgroundColor != other.sheetBackgroundColor) return false
        if (sheetContentColor != other.sheetContentColor) return false
        if (scrimColor != other.scrimColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modifier.hashCode()
        result = 31 * result + sheetState.hashCode()
        result = 31 * result + sheetShape.hashCode()
        result = 31 * result + sheetElevation.hashCode()
        result = 31 * result + sheetBackgroundColor.hashCode()
        result = 31 * result + sheetContentColor.hashCode()
        result = 31 * result + scrimColor.hashCode()
        return result
    }
}
