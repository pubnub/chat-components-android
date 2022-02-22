package com.pubnub.components.repository.member

import androidx.paging.PagingSource
import com.pubnub.components.data.member.Member
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface MemberRepository<DB : Member, Data : Member> {
    suspend fun get(id: UserId): Data?
    fun getAll(
        id: ChannelId? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, Data>

    suspend fun getList(id: ChannelId? = null): List<Data>
    suspend fun insertOrUpdate(vararg member: DB)
    suspend fun remove(id: UserId)
    suspend fun size(): Long
}
