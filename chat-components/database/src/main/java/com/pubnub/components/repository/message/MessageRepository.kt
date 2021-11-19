package com.pubnub.components.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import com.pubnub.components.data.message.Message
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
interface MessageRepository<DB : Message, Data : Message> {
    suspend fun get(messageId: String): Data?
    suspend fun getList(
        channelId: String,
        count: Int,
        before: Boolean,
        timestamp: Long,
    ): List<Data>

    fun getAll(
        channelId: String? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, Data>

    suspend fun getLast(channelId: String): Data?
    fun getLastByChannel(channelId: String, count: Long): Flow<List<Data>>

    suspend fun hasMoreBefore(channelId: String, timestamp: Timetoken): Boolean
    suspend fun hasMoreAfter(channelId: String, timestamp: Timetoken): Boolean


    suspend fun add(message: DB)
    suspend fun remove(message: DB)
    suspend fun removeAll(channel: ChannelId)
    suspend fun update(message: DB)
    suspend fun has(messageId: String): Boolean

    suspend fun setSent(
        messageId: String,
        timestamp: Timetoken? = null,
    )

    suspend fun setSendingError(
        messageId: String,
        exception: String? = null,
        timestamp: Timetoken? = null,
    )

    suspend fun getLastTimestamp(channelId: String): Long
}
