package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.repository.message.DefaultMessageRepository

val LocalMessageRepository =
    staticCompositionLocalOf<DefaultMessageRepository> { throw MessageRepositoryNotInitializedException() }

class MessageRepositoryNotInitializedException : Exception("Message repository not initialized")
