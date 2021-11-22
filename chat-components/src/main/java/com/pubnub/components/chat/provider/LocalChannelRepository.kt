package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.repository.channel.DefaultChannelRepository

val LocalChannelRepository =
    staticCompositionLocalOf<DefaultChannelRepository> { throw ChannelRepositoryNotInitializedException() }

class ChannelRepositoryNotInitializedException : Exception("Channel repository not initialized")
