package com.pubnub.components.data.membership

import androidx.paging.PagingSource
import androidx.room.*
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

interface MembershipDao<Data : Membership> {

    @Query("SELECT * FROM `membership` WHERE id LIKE :membershipId LIMIT 1")
    suspend fun get(membershipId: String): Data?

    @Query("SELECT * FROM `membership` WHERE memberId LIKE :memberId")
    fun getAll(memberId: UserId): Flow<List<Data>>

    @Query("SELECT * FROM `membership` WHERE channelId LIKE :channelId")
    fun getAllByChannel(channelId: ChannelId): PagingSource<Int, Data>

    @Query("SELECT * FROM `membership` WHERE memberId LIKE :memberId")
    fun getAllByMember(memberId: UserId): PagingSource<Int, Data>

    @Query("SELECT * FROM `membership`")
    fun getList(): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: Data)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg data: Data)

    @Delete
    suspend fun delete(data: Data)

    @Query("DELETE FROM `membership` WHERE id LIKE :membershipId")
    suspend fun delete(membershipId: String)

    @Query("SELECT COUNT(*) FROM `membership`")
    suspend fun size(): Long
}
