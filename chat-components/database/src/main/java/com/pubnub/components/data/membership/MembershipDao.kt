package com.pubnub.components.data.membership

import androidx.paging.PagingSource
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MembershipId
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

interface MembershipDao<Data : Membership> {

    @Query("SELECT * FROM `membership` WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: MembershipId): Data?

    @Query("SELECT * FROM `membership` WHERE memberId LIKE :id")
    fun getAll(id: UserId): Flow<List<Data>>

    @Query("SELECT * FROM `membership` WHERE channelId LIKE :id")
    fun getAllByChannel(id: ChannelId): PagingSource<Int, Data>

    @Query("SELECT * FROM `membership` WHERE memberId LIKE :id")
    fun getAllByMember(id: UserId): PagingSource<Int, Data>

    @Query("SELECT * FROM `membership`")
    fun getList(): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg data: Data)

    @Delete
    suspend fun delete(data: Data)

    @Query("DELETE FROM `membership` WHERE id LIKE :id")
    suspend fun delete(id: MembershipId)

    @Query("SELECT COUNT(*) FROM `membership`")
    suspend fun size(): Long
}
