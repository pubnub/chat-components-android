package com.pubnub.components.chat.network.mapper

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.pubnub.api.managers.MapperManager
import com.pubnub.api.models.consumer.history.PNFetchMessageItem
import com.pubnub.components.chat.network.data.NetworkMessage
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.toJson
import java.lang.reflect.Type

class NetworkMessageHistoryMapper(private val mapper: MapperManager) :
    Mapper<PNFetchMessageItem, DBMessage> {
    lateinit var channel: ChannelId

    private val gson = GsonBuilder()
        .registerTypeAdapter(NetworkMessage.Attachment::class.java, attachmentDeserializer())
        .enableComplexMapKeySerialization()
        .create()

    @Suppress("UNCHECKED_CAST")
    override fun map(input: PNFetchMessageItem): DBMessage {
        val message: NetworkMessage =
            gson.fromJson(input.message.toJson(mapper), NetworkMessage::class.java)
        return DBMessage(
            id = message.id,
            text = message.text,
            type = message.type,
            attachment = message.attachment.toDb(),
            custom = message.custom as Map<String, Any>?,
            publisher = input.uuid!!,
            channel = channel,
            timetoken = input.timetoken,
            isSent = true,
            exception = null,
        )
    }

    private fun attachmentDeserializer(): JsonDeserializer<NetworkMessage.Attachment> =
        JsonDeserializer { jsonElement: JsonElement, _: Type, _: JsonDeserializationContext ->
            val jsonObject = jsonElement.asJsonObject

            when (val objectType = jsonObject.get("type").asString) {
                "link" -> gson.fromJson(
                    jsonElement.toString(),
                    NetworkMessage.Attachment.Link::class.java
                )
                "image" -> gson.fromJson(
                    jsonElement.toString(),
                    NetworkMessage.Attachment.Image::class.java
                )
                "custom" -> gson.fromJson(
                    jsonElement.toString(),
                    NetworkMessage.Attachment.Custom::class.java
                )
                else -> throw RuntimeException("Unknown type '$objectType'")
            }
        }
}