package com.pubnub.components.repository.channel

import androidx.paging.PagingSource
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface ChannelRepository<in IN : Channel, OUT : Channel> {
    suspend fun get(id: ChannelId): OUT?
    fun getAll(
        id: UserId? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, OUT>

    suspend fun getList(): List<OUT>
    suspend fun insertOrUpdate(vararg channel: IN)
    suspend fun remove(id: ChannelId)
    suspend fun size(): Long
}
