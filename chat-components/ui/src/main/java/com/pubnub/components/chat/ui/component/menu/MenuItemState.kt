package com.pubnub.components.chat.ui.component.menu

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.painter.Painter

data class MenuItemState(
    @StringRes val title: Int,
    val iconPainter: Painter,
    val action: MenuAction,
)