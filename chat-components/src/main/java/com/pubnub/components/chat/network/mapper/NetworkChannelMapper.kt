package com.pubnub.components.chat.network.mapper

import com.pubnub.api.models.consumer.objects.channel.PNChannelMetadata
import com.pubnub.components.chat.network.data.NetworkChannelMetadata
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.message.asMap
import com.pubnub.framework.mapper.Mapper

class NetworkChannelMapper :
    Mapper<NetworkChannelMetadata, DBChannel> {

    override fun map(input: PNChannelMetadata): DBChannel {
        val custom = input.custom.asMap()
        val type = input.type ?: ((custom?.get("type") as? String?) ?: "default")
        val profileUrl = (custom?.get("profileUrl") as? String)
        val channelCustom = custom?.apply {
            remove("profileUrl")
        }
        return DBChannel(
            id = input.id,
            name = input.name,
            description = input.description,
            type = type,
            status = input.status,
            custom = channelCustom,
            profileUrl = profileUrl,
            eTag = input.eTag,
            updated = input.updated,
        )
    }
}
