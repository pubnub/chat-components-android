package com.pubnub.components.chat.ui.component.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pubnub.components.chat.ui.component.input.DefaultLocalMessageInputTheme
import com.pubnub.components.chat.ui.component.input.LocalMessageInputTheme

@Composable
fun ChatComponentsTheme(content: @Composable() () -> Unit) {
    CompositionLocalProvider(LocalMessageInputTheme provides DefaultLocalMessageInputTheme) {
        content()
    }
}