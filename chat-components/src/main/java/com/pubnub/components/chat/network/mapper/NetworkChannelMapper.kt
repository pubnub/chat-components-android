package com.pubnub.components.chat.network.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pubnub.api.managers.MapperManager
import com.pubnub.api.models.consumer.objects.channel.PNChannelMetadata
import com.pubnub.components.data.channel.ChannelCustomData
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.asObject

class NetworkChannelMapper(private val mapper: MapperManager? = null) :
    Mapper<PNChannelMetadata, DBChannel> {

    private fun getCustomData(custom: Any?): ChannelCustomData =
        if (mapper != null) {
            custom.asObject(mapper)
        } else {
            val gson = Gson()
            val typeToken = object : TypeToken<ChannelCustomData>() {}.type
            gson.fromJson(gson.toJson(custom), typeToken)
        }


    override fun map(input: PNChannelMetadata): DBChannel {
        val custom: ChannelCustomData = getCustomData(input.custom)

        val type = (custom["type"] as? String) ?: "default"
        val avatarURL = (custom["avatarURL"] as String)
        val channelCustom = custom.apply {
            this.remove("type")
            this.remove("avatarURL")
        }
        return DBChannel(
            id = input.id,
            name = input.name!!,
            description = input.description,
            type = type,
            updated = input.updated,
            eTag = input.eTag,
            avatarURL = avatarURL,
            custom = channelCustom,
        )
    }
}
