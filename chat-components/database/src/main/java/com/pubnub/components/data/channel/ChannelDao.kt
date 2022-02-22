package com.pubnub.components.data.channel

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface ChannelDao<DB : Channel, Data : Channel> {
    @Transaction
    @Query("SELECT * FROM `channel` WHERE channelId LIKE :id LIMIT 1")
    suspend fun get(id: ChannelId): Data?

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, Data>

    @Transaction
    @Query("SELECT * FROM `channel`")
    suspend fun getList(): List<Data>

    @Transaction
    @Query("SELECT * FROM `channel` WHERE channelId IN (SELECT channelId FROM `membership` WHERE memberId LIKE :id)")
    fun getList(id: UserId): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg data: DB)

    @Delete
    suspend fun delete(data: DB)

    @Transaction
    @Query("DELETE FROM `channel` WHERE channelId LIKE :id")
    suspend fun delete(id: ChannelId)

    @Transaction
    @Query("SELECT COUNT(*) FROM `channel`")
    suspend fun size(): Long
}