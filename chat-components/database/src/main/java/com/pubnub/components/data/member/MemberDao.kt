package com.pubnub.components.data.member

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface MemberDao<DB : Member, Data : Member> {
    @Transaction
    @Query("SELECT * FROM `member` WHERE memberId LIKE :id LIMIT 1")
    suspend fun get(id: UserId): Data?

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, Data>

    @Transaction
    @Query("SELECT * FROM `member` ORDER BY name")
    fun getList(): List<Data>

    @Transaction
    @Query("SELECT * FROM `member` WHERE memberId IN (SELECT memberId FROM `membership` WHERE channelId LIKE :id) ORDER BY name")
    fun getList(id: ChannelId): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg data: DB)

    @Delete
    suspend fun delete(data: DB)

    @Transaction
    @Query("DELETE FROM `member` WHERE memberId LIKE :id")
    suspend fun delete(id: UserId)

    @Transaction
    @Query("SELECT COUNT(*) FROM `member`")
    suspend fun size(): Long
}