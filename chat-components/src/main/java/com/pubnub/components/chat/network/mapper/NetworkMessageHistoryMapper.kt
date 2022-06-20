package com.pubnub.components.chat.network.mapper

import com.google.gson.GsonBuilder
import com.pubnub.api.managers.MapperManager
import com.pubnub.api.models.consumer.history.PNFetchMessageItem
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.data.message.DBCustomContent
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.MapperWithId
import com.pubnub.framework.util.toJson

class NetworkMessageHistoryMapper(private val mapper: MapperManager) :
    MapperWithId<PNFetchMessageItem, DBMessage> {

    private val gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .create()

    @Suppress("UNCHECKED_CAST")
    override fun map(id: ChannelId, input: PNFetchMessageItem): DBMessage {
        val messagePayload: NetworkMessagePayload =
            gson.fromJson(input.message.toJson(mapper), NetworkMessagePayload::class.java)
        return DBMessage(
            id = messagePayload.id,
            text = messagePayload.text,
            contentType = messagePayload.contentType,
            content = messagePayload.content as DBCustomContent?,
            custom = messagePayload.custom as DBCustomContent?,
            publisher = input.uuid!!,
            channel = id,
            timetoken = input.timetoken,
            isSent = true,
            exception = null,
        )
    }
}
