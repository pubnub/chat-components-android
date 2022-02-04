package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.repository.message.DefaultMessageRepository
import com.pubnub.components.repository.message.MessageRepository

val LocalMessageRepository =
    staticCompositionLocalOf<MessageRepository<DBMessage, DBMessageWithActions>> { throw MessageRepositoryNotInitializedException() }

class MessageRepositoryNotInitializedException : Exception("Message repository not initialized")
