package com.pubnub.components.data.member

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

interface MemberDao<IN : Member, OUT : Member> {
    @Transaction
    @Query("SELECT * FROM `member` WHERE memberId LIKE :id LIMIT 1")
    suspend fun get(id: UserId): OUT?

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, OUT>

    @Transaction
    @Query("SELECT * FROM `member` ORDER BY name")
    fun getList(): List<OUT>

    @Transaction
    @Query("SELECT * FROM `member` WHERE memberId IN (SELECT memberId FROM `membership` WHERE channelId LIKE :id) ORDER BY name")
    fun getList(id: ChannelId): List<OUT>

    @Upsert
    suspend fun insertOrUpdate(vararg data: IN)

    @Delete
    suspend fun delete(data: IN)

    @Transaction
    @Query("DELETE FROM `member` WHERE memberId LIKE :id")
    suspend fun delete(id: UserId)

    @Transaction
    @Query("SELECT COUNT(*) FROM `member`")
    suspend fun size(): Long
}