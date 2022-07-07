package com.pubnub.components.chat.ui.component.input.renderer

import androidx.compose.runtime.Composable
import com.pubnub.components.chat.ui.component.input.TypingUi

interface TypingIndicatorRenderer {

    @Composable
    fun TypingIndicator(data: List<TypingUi>)
}
