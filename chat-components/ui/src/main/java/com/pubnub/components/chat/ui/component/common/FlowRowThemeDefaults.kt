package com.pubnub.components.chat.ui.component.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object FlowRowThemeDefaults {
    fun reactionList() = FlowRowTheme(
        modifier = Modifier,
        mainAxisSpacing = 6.dp,
        crossAxisSpacing = 6.dp,
    )
}