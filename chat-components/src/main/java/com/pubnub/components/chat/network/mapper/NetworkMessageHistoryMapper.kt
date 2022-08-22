package com.pubnub.components.chat.network.mapper

import com.google.gson.GsonBuilder
import com.pubnub.api.managers.MapperManager
import com.pubnub.components.chat.network.data.NetworkHistoryMessage
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.MapperWithId
import com.pubnub.framework.util.toJson

class NetworkMessageHistoryMapper(private val mapper: MapperManager) :
    MapperWithId<NetworkHistoryMessage, DBMessage> {

    private val gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .create()

    @Suppress("UNCHECKED_CAST")
    override fun map(id: ChannelId, input: NetworkHistoryMessage): DBMessage {
        val messagePayload: NetworkMessagePayload =
            gson.fromJson(input.message.toJson(mapper), NetworkMessagePayload::class.java)
        return DBMessage(
            id = messagePayload.id,
            text = messagePayload.text,
            contentType = messagePayload.contentType,
            content = messagePayload.content,
            createdAt = messagePayload.createdAt,
            custom = messagePayload.custom,
            publisher = input.uuid!!,
            channel = id,
            isSent = true,
            exception = null,
        )
    }
}
