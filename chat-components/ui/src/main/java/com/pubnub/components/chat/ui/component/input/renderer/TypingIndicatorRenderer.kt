package com.pubnub.components.chat.ui.component.input.renderer

import androidx.compose.runtime.Composable
import com.pubnub.framework.data.Typing

interface TypingIndicatorRenderer {

    @Composable
    fun TypingIndicator(data: List<Typing>)
}
