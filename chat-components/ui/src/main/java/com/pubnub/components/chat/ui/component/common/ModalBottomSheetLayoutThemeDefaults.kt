package com.pubnub.components.chat.ui.component.common

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterialApi::class)
object ModalBottomSheetLayoutThemeDefaults {
    @Composable
    fun reaction(
        modifier: Modifier = Modifier,
        sheetState: ModalBottomSheetState =
            rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
        sheetShape: Shape = MaterialTheme.shapes.large,
        sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
        sheetBackgroundColor: Color = MaterialTheme.colors.surface,
        sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
        scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    ) = ModalBottomSheetLayoutTheme(
        modifier,
        sheetState,
        sheetShape,
        sheetElevation,
        sheetBackgroundColor,
        sheetContentColor,
        scrimColor
    )
}