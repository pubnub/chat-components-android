package com.pubnub.components.chat.ui.component.channel.renderer

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ChannelRenderer {
    @Composable
    fun Channel(
        name: String,
        description: String,
        profileUrl: String,
        onClick: () -> Unit,
        onLeave: (() -> Unit)?,
        modifier: Modifier
    )

    @Composable
    fun Separator(title: String?, onClick: (() -> Unit)? = null) {
    }

    @Composable
    fun Placeholder() {
    }

    fun renderSeparator(scope: LazyListScope, title: String?, onClick: (() -> Unit)? = null) {}
}