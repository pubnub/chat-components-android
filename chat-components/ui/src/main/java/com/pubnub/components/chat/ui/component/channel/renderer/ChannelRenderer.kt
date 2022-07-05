package com.pubnub.components.chat.ui.component.channel.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ChannelRenderer {
    @Composable
    fun Channel(
        name: String,
        description: String?,
        modifier: Modifier,
        profileUrl: String?,
        onClick: (() -> Unit)?,
        onLeave: (() -> Unit)?,
    )

    @Composable
    fun Separator(title: String?, onClick: (() -> Unit)? = null) {
    }

    @Composable
    fun Placeholder() {
    }
}
