package com.pubnub.components.chat.service.channel

import com.pubnub.api.PubNub
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.service.error.Logger
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class DefaultChannelService(
    private val pubNub: PubNub,
    private val logger: Logger,
) : ChannelService<DBChannel> {

    private var subscribedChannels: List<String> = listOf()

    override fun bind(vararg channels: String) {
        logger.i("Subscribing to the channels '${channels.joinToString()}'")
        subscribedChannels = listOf(*channels)
        pubNub.subscribe(channels = subscribedChannels, withPresence = true)
    }

    override fun unbind() {
        logger.i("Unsubscribing from channels '${subscribedChannels.joinToString()}'")
        pubNub.unsubscribe(channels = subscribedChannels)
    }
}