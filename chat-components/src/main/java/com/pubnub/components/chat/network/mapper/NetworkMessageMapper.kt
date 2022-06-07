package com.pubnub.components.chat.network.mapper

import com.pubnub.api.managers.MapperManager
import com.pubnub.components.chat.network.data.NetworkMessage
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.data.message.DBCustomContent
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.asObject

class NetworkMessageMapper(private val mapper: MapperManager) : Mapper<NetworkMessage, DBMessage> {

    @Suppress("UNCHECKED_CAST")
    override fun map(input: NetworkMessage): DBMessage {

        val messagePayload: NetworkMessagePayload = input.message.asObject(mapper)
        return DBMessage(
            id = messagePayload.id,
            text = messagePayload.text,
            contentType = messagePayload.contentType,
            content = messagePayload.content as DBCustomContent,
            custom = messagePayload.custom as DBCustomContent?,
            publisher = input.publisher!!,
            channel = input.channel,
            timetoken = input.timetoken!!,
            isSent = true,
            exception = null,
        )
    }
}
