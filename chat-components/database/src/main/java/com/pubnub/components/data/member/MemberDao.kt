package com.pubnub.components.data.member

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId

interface MemberDao<DB : Member, Data : Member> {
    @Transaction
    @Query("SELECT * FROM `member` WHERE memberId LIKE :memberId LIMIT 1")
    suspend fun get(memberId: String): Data?
//
//    @Transaction
//    @Query("SELECT * FROM `member` ORDER BY name")
//    fun getAll(): PagingSource<Int, Data>

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, Data>

    @Transaction
    @Query("SELECT * FROM `member` ORDER BY name")
    fun getList(): List<Data>

    @Transaction
    @Query("SELECT * FROM `member` WHERE memberId IN (SELECT memberId FROM `membership` WHERE channelId LIKE :channelId) ORDER BY name")
    fun getList(channelId: ChannelId): List<Data>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: DB)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg data: DB)

    @Delete
    suspend fun delete(data: DB)

    @Transaction
    @Query("DELETE FROM `member` WHERE memberId LIKE :memberId")
    suspend fun delete(memberId: String)

    @Transaction
    @Query("SELECT COUNT(*) FROM `member`")
    suspend fun size(): Long
}