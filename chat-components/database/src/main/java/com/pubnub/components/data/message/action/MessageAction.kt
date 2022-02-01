package com.pubnub.components.data.message.action

import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken

interface MessageAction {
    val id: String
    val channel: ChannelId
    val user: UserId
    val messageTimestamp: Timetoken
    val published: Timetoken
    val type: String
    val value: String
}
