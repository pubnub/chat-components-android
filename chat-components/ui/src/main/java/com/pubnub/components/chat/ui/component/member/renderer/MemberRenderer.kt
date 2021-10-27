package com.pubnub.components.chat.ui.component.member.renderer

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface MemberRenderer {
    @Composable
    fun Member(
        name: String,
        description: String,
        profileUrl: String,
        online: Boolean?,
        onClick: () -> Unit,
        modifier: Modifier
    )

    @Composable
    fun Placeholder() {
    }

    @Composable
    fun Separator(title: String) {
    }

    fun renderSeparator(scope: LazyListScope, title: String?)
}