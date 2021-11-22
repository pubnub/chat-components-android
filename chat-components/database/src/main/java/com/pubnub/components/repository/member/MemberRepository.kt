package com.pubnub.components.repository.member

import androidx.paging.PagingSource
import com.pubnub.components.data.member.Member
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface MemberRepository<DB : Member, Data : Member> {
    suspend fun get(userId: UserId): Data?
    fun getAll(
        channelId: ChannelId? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, Data>

    suspend fun getList(channelId: ChannelId? = null): List<Data>
    suspend fun add(vararg member: DB)
    suspend fun remove(id: String)
    suspend fun size(): Long
}