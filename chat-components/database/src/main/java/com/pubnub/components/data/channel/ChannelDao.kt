package com.pubnub.components.data.channel

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface ChannelDao<IN : Channel, OUT : Channel> {
    @Transaction
    @Query("SELECT * FROM `channel` WHERE channelId LIKE :id LIMIT 1")
    suspend fun get(id: ChannelId): OUT?

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, OUT>

    @Transaction
    @Query("SELECT * FROM `channel`")
    suspend fun getList(): List<OUT>

    @Transaction
    @Query("SELECT * FROM `channel` WHERE channelId IN (SELECT channelId FROM `membership` WHERE memberId LIKE :id)")
    fun getList(id: UserId): List<OUT>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg data: IN)

    @Delete
    suspend fun delete(data: IN)

    @Transaction
    @Query("DELETE FROM `channel` WHERE channelId LIKE :id")
    suspend fun delete(id: ChannelId)

    @Transaction
    @Query("SELECT COUNT(*) FROM `channel`")
    suspend fun size(): Long
}