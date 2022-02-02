package com.pubnub.components.repository.message.action

import com.pubnub.components.data.message.action.MessageAction
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken

interface MessageActionRepository<DB : MessageAction> {
    suspend fun get(
        user: UserId,
        channel: ChannelId,
        messageTimetoken: Timetoken,
        type: String,
        value: String,
    ): DB?

    suspend fun has(id: String): Boolean
    suspend fun add(vararg action: DB)
    suspend fun remove(vararg action: DB)
    suspend fun insertUpdate(vararg data: DB)
    suspend fun getLastTimetoken(channel: ChannelId): Timetoken
}