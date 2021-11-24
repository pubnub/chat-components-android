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
interface MessageRepository<DB : Message, Data : Message> {
    suspend fun get(id: MessageId): Data?
    suspend fun getList(
        id: ChannelId,
        count: Int,
        before: Boolean,
        timestamp: Timetoken,
    ): List<Data>
    fun getAll(
        id: ChannelId? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, Data>
    suspend fun getLast(id: ChannelId): Data?
    fun getLastByChannel(id: ChannelId, count: Long): Flow<List<Data>>
    suspend fun has(id: MessageId): Boolean
    suspend fun hasMoreBefore(id: ChannelId, timestamp: Timetoken): Boolean
    suspend fun hasMoreAfter(id: ChannelId, timestamp: Timetoken): Boolean
    suspend fun add(vararg message: DB)
    suspend fun remove(message: DB)
    suspend fun removeAll(id: ChannelId)
    suspend fun update(message: DB)
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
