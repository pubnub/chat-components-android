package com.pubnub.components.chat.service.message

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.data.message.Message
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken

interface MessageService<Data : Message> {
    fun bind()
    fun unbind()

    @Suppress("NAME_SHADOWING")
    suspend fun send(
        channelId: ChannelId,
        message: Data,
        meta: Any? = null,
        store: Boolean = true,
        onSuccess: (String, Timetoken) -> Unit = { _: String, _: Timetoken -> },
        onError: (Exception) -> Unit = {},
    )

    suspend fun fetchAll(
        id: ChannelId,
        start: Long?,
        end: Long?,
        count: Int,
        withActions: Boolean = true,
        withUUID: Boolean = false,
    )
}

val LocalMessageService =
    staticCompositionLocalOf<MessageService<NetworkMessagePayload>> { throw MessageServiceNotInitializedException() }

class MessageServiceNotInitializedException :
    Exception("Message Service not initialized")