package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.repository.message.action.DefaultMessageActionRepository

val LocalMessageActionRepository =
    staticCompositionLocalOf<DefaultMessageActionRepository> { throw MessageActionRepositoryNotInitializedException() }

class MessageActionRepositoryNotInitializedException : Exception("MessageAction repository not initialized")
