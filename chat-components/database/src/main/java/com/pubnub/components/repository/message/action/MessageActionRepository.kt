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
    suspend fun remove(vararg action: DB)
    suspend fun insertOrUpdate(vararg action: DB)
    suspend fun getLastTimetoken(channel: ChannelId): Timetoken

    fun runInTransaction(body: () -> Unit)
}
