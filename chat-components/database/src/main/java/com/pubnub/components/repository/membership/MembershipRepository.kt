package com.pubnub.components.repository.membership

import com.pubnub.components.data.membership.Membership
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

interface MembershipRepository<DB : Membership> {
    suspend fun get(userId: UserId): DB?
    fun getAll(userId: UserId): Flow<List<DB>>
    suspend fun getList(): List<DB>
    suspend fun add(vararg membership: DB)
    suspend fun remove(id: String)
    suspend fun size(): Long
}