package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.data.channel.DBChannel

interface ChannelService<Data : Channel> {
    fun bind(vararg channels: String)
    fun unbind()
}

val LocalChannelService =
    staticCompositionLocalOf<ChannelService<DBChannel>> { throw ChannelServiceNotInitializedException() }

class ChannelServiceNotInitializedException :
    Exception("Channel Service not initialized")