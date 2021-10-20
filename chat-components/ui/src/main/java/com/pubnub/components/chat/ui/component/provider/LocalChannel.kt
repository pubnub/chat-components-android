package com.pubnub.components.chat.ui.component.provider

import androidx.compose.runtime.compositionLocalOf

val LocalChannel = compositionLocalOf<String> { throw MissingChannelException() }

class MissingChannelException : Exception("No channel provided.")
