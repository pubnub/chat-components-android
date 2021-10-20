package com.pubnub.components.data.message

import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken

interface Message {
    val id: MessageId
    val type: String
    val text: String?
    val attachment: List<Attachment>?
    val custom: Any?

    val publisher: UserId
    val channel: ChannelId
    val timetoken: Timetoken
    val isSent: Boolean
    var exception: String?
}

interface Attachment {
    val type: String
    val custom: Any?
}