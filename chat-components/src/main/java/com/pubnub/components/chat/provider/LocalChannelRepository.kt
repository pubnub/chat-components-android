package com.pubnub.components.chat.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.channel.ChannelRepository

val LocalChannelRepository =
    compositionLocalOf<ChannelRepository<DBChannel, DBChannelWithMembers>> { throw ChannelRepositoryNotInitializedException() }

class ChannelRepositoryNotInitializedException : Exception("Channel repository not initialized")
