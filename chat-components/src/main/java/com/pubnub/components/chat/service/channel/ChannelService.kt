package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.data.channel.DBChannel

interface ChannelService<Data : Channel> {
    fun bind(vararg channels: String)
    fun unbind()
}

val LocalChannelService =
    compositionLocalOf<ChannelService<DBChannel>> { throw ChannelServiceNotInitializedException() }

class ChannelServiceNotInitializedException :
    Exception("Channel Service not initialized")