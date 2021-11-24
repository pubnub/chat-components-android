package com.pubnub.components.repository.channel

import androidx.paging.PagingSource
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface ChannelRepository<DB : Channel, Data : Channel> {
    suspend fun get(id: ChannelId): Data?
    fun getAll(
        id: UserId? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, Data>

    suspend fun getList(): List<Data>
    suspend fun add(vararg channel: DB)
    suspend fun remove(id: ChannelId)
    suspend fun size(): Long
}
