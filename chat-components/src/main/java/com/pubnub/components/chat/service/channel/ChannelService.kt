package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.data.ChannelGroupId
import com.pubnub.framework.data.ChannelId

interface ChannelService<Data : Channel> {
    fun bind(channels: List<ChannelId> = emptyList(), channelGroups: List<ChannelGroupId> = emptyList(), withPresence: Boolean = false)
    fun unbind()
}

val LocalChannelService =
    compositionLocalOf<ChannelService<DBChannel>> { throw ChannelServiceNotInitializedException() }

class ChannelServiceNotInitializedException :
    Exception("Channel Service not initialized")