package com.pubnub.components.data.channel

import com.pubnub.framework.data.ChannelId

interface Channel {
    val id: ChannelId           // used as an unique channel identifier across the components
    val name: String            // displayed on the ChannelList (first line)
    val description: String?    // displayed on the ChannelList (second line)
    val updated: String?
    val eTag: String?
    val type: String
    val avatarURL: String?
    val custom: Any?            // not used in Chat Components by default, but can store additional data
}