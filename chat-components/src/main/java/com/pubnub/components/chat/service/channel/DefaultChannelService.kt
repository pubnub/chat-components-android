package com.pubnub.components.chat.service.channel

import com.pubnub.api.PubNub
import com.pubnub.components.data.channel.DBChannel
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class DefaultChannelService(
    val pubNub: PubNub,
) : ChannelService<DBChannel> {

    private var subscribedChannels: List<String> = listOf()

    override fun bind(vararg channels: String) {
        subscribedChannels = listOf(*channels)
        pubNub.subscribe(channels = subscribedChannels, withPresence = true)
    }

    override fun unbind() {
        pubNub.unsubscribe(channels = subscribedChannels)
    }
}