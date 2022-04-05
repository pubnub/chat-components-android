package com.pubnub.components.repository.member

import androidx.paging.PagingSource
import com.pubnub.components.data.member.Member
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface MemberRepository<in IN : Member, OUT : Member> {
    suspend fun get(id: UserId): OUT?
    fun getAll(
        id: ChannelId? = null,
        filter: Query? = null,
        vararg sorted: Sorted = emptyArray(),
    ): PagingSource<Int, OUT>

    suspend fun getList(id: ChannelId? = null): List<OUT>
    suspend fun insertOrUpdate(vararg member: IN)
    suspend fun remove(id: UserId)
    suspend fun size(): Long
}
