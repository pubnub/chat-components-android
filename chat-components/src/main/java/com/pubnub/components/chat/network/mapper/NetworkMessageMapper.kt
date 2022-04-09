package com.pubnub.components.chat.network.mapper

import com.pubnub.api.managers.MapperManager
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.components.chat.network.data.NetworkMessage
import com.pubnub.components.data.message.DBAttachment
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.asObject

class NetworkMessageMapper(private val mapper: MapperManager) : Mapper<PNMessageResult, DBMessage> {

    @Suppress("UNCHECKED_CAST")
    override fun map(input: PNMessageResult): DBMessage {

        val message: NetworkMessage = input.message.asObject(mapper)
        return DBMessage(
            id = message.id,
            text = message.text,
            type = message.type ?: NetworkMessage.Type.DEFAULT,
            attachment = message.attachment.toDb(),
            custom = message.custom as Map<String, Any>?,
            publisher = input.publisher!!,
            channel = input.channel,
            timetoken = input.timetoken!!,
            isSent = true,
            exception = null,
        )
    }
}


fun List<NetworkMessage.Attachment>?.toDb(): List<DBAttachment>? =
    this?.mapNotNull { it.toDb() }

fun NetworkMessage.Attachment?.toDb(): DBAttachment? =
    when (this) {
        is NetworkMessage.Attachment.Image -> DBAttachment.Image(
            imageUrl = imageUrl,
            custom = custom
        )
        is NetworkMessage.Attachment.Link -> DBAttachment.Link(link = link, custom = custom)
        is NetworkMessage.Attachment.Custom -> DBAttachment.Custom(custom = custom)
        else -> null
    }

fun List<DBAttachment>?.toNetwork(): List<NetworkMessage.Attachment>? =
    this?.map { it.toNetwork() }

fun DBAttachment.toNetwork(): NetworkMessage.Attachment =
    when (this) {
        is DBAttachment.Image -> NetworkMessage.Attachment.Image(
            imageUrl = imageUrl,
            custom = custom
        )
        is DBAttachment.Link -> NetworkMessage.Attachment.Link(link = link, custom = custom)
        else -> NetworkMessage.Attachment.Custom(custom = custom)
    }
