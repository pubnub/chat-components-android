package com.pubnub.components.chat.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository

val LocalMessageActionRepository =
    compositionLocalOf<MessageActionRepository<DBMessageAction>> { throw MessageActionRepositoryNotInitializedException() }

class MessageActionRepositoryNotInitializedException :
    Exception("MessageAction repository not initialized")
