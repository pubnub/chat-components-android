package com.pubnub.components.repository.membership

import com.pubnub.components.data.membership.Membership
import com.pubnub.framework.data.MembershipId
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

interface MembershipRepository<DB : Membership> {
    suspend fun get(id: MembershipId): DB?
    fun getAll(id: UserId): Flow<List<DB>>
    suspend fun getList(): List<DB>
    suspend fun add(vararg membership: DB)
    suspend fun remove(id: MembershipId)
    suspend fun size(): Long
}
