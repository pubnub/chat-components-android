package com.pubnub.components.data.sync

import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken

interface RemoteTimetoken {
    val table: String
    val channelId: ChannelId
    val start: Timetoken
    val end: Timetoken
}
