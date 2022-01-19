package com.pubnub.components.chat.service.message

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.Message
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken
import timber.log.Timber

interface MessageService<Data : Message> {
    fun bind()
    fun unbind()

    @Suppress("NAME_SHADOWING")
    suspend fun send(
        id: ChannelId,
        message: Data,
        meta: Any? = null,
        store: Boolean = true,
        onSuccess: (String, Timetoken) -> Unit = { message, result -> Timber.i("Message '$message' sent, result: $result") },
        onError: (Exception) -> Unit = { Timber.i("Message sending error: $it") },
    )

    suspend fun pullHistory(
        id: ChannelId,
        start: Long?,
        end: Long?,
        count: Int,
        withActions: Boolean = false,
        withUUID: Boolean = false
    )
}

val LocalMessageService =
    staticCompositionLocalOf<MessageService<DBMessage>> { throw MessageServiceNotInitializedException() }

class MessageServiceNotInitializedException :
    Exception("Message Service not initialized")