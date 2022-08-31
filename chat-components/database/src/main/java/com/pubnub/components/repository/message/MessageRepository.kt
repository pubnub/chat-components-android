package com.pubnub.components.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import com.pubnub.components.data.message.Message
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
interface MessageRepository<in IN : Message, OUT : Message> {
    suspend fun get(id: MessageId): OUT?
    suspend fun getList(
        id: ChannelId,
        count: Int,
        before: Boolean,
        timestamp: Timetoken,
    ): List<OUT>

    fun getAll(
        id: ChannelId? = null,
        contentType: String? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, OUT>

    suspend fun getLast(id: ChannelId): OUT?
    fun getLastByChannel(id: ChannelId, count: Long): Flow<List<OUT>>
    suspend fun has(id: MessageId): Boolean
    suspend fun hasMoreBefore(id: ChannelId, timestamp: Timetoken): Boolean
    suspend fun hasMoreAfter(id: ChannelId, timestamp: Timetoken): Boolean
    suspend fun remove(message: IN)
    suspend fun removeAll(id: ChannelId)
    suspend fun insertOrUpdate(vararg message: IN)
    suspend fun setSent(
        id: MessageId,
        timestamp: Timetoken? = null,
    )

    suspend fun setSendingError(
        id: MessageId,
        exception: String? = null,
        timestamp: Timetoken? = null,
    )

    suspend fun getLastTimestamp(id: ChannelId): Timetoken
}
