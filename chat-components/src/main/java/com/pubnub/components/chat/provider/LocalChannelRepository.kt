package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.channel.ChannelRepository
import com.pubnub.components.repository.channel.DefaultChannelRepository

val LocalChannelRepository =
    staticCompositionLocalOf<ChannelRepository<DBChannel, DBChannelWithMembers>> { throw ChannelRepositoryNotInitializedException() }

class ChannelRepositoryNotInitializedException : Exception("Channel repository not initialized")
