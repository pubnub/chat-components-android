package com.pubnub.components.chat.service.channel

import com.pubnub.api.PubNub
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.data.ChannelGroupId
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.service.error.Logger

class DefaultChannelService(
    private val pubNub: PubNub,
    private val logger: Logger,
) : ChannelService<DBChannel> {

    private var subscribedChannels: List<ChannelId> = listOf()
    private var subscribedChannelGroups: List<ChannelGroupId> = listOf()

    /**
     * Subscribes to channels and channel groups
     *
     * @param channels - Channels to subscribe/unsubscribe. Either channel or channelGroups are required.
     * @param channelGroups - Channel groups to subscribe/unsubscribe. Either channelGroups or channels are required.
     * @param withPresence - Also subscribe to related presence channel.
     */
    override fun bind(channels: List<ChannelId>, channelGroups: List<ChannelGroupId>, withPresence: Boolean) {
        logger.i("Subscribing to the channels '${channels.joinToString()}' and groups '${channelGroups.joinToString()}'")
        subscribedChannels = channels
        subscribedChannelGroups = channelGroups
        pubNub.subscribe(
            channels = subscribedChannels,
            channelGroups = channelGroups,
            withPresence = withPresence,
        )
    }

    /**
     * Unsubscribes from a channels and channel groups
     */
    override fun unbind() {
        logger.i("Unsubscribing from channels '${subscribedChannels.joinToString()}' and groups '${subscribedChannelGroups.joinToString()}'")
        pubNub.unsubscribe(channels = subscribedChannels, channelGroups = subscribedChannelGroups)
    }
}