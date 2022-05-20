package com.pubnub.components.data.channel

import com.pubnub.framework.data.ChannelId

interface Channel {
    val id: ChannelId           // The unique identifier for a channel
    val name: String?           // Name of the channel that could be displayed in the UI
    val description: String?    // Description of the channel that could be displayed in the UI
    val type: String            // Type of the channel, ["default", "group", "direct"]
    val status: String?         // Status of the channel, ["default", "deleted"]
    val custom: Any?            // Any additional payload possibly needed and provided by end users
    val profileUrl: String?     // URL to a channel avatar that could be displayed in the UI
    val updated: String?
    val eTag: String?
}