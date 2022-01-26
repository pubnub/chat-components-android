package com.pubnub.components.data.message.action

import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface MessageAction {
    val id: String
    val channel: ChannelId
    val user: UserId
    val messageTimestamp: Long
    val actionTimestamp: Long
    val type: String
    val action: String
}
