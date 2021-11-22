package com.pubnub.components.data.message

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

interface MessageDao<DB : Message, Data : Message> {

    @Transaction
    @Query("SELECT * FROM message WHERE id LIKE :messageId LIMIT 1")
    suspend fun get(messageId: String): Data?

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :channelId AND timetoken < :timestamp ORDER BY timetoken DESC LIMIT :count")
    suspend fun getBefore(channelId: String, count: Int, timestamp: Long): List<Data>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :channelId AND timetoken > :timestamp ORDER BY timetoken DESC LIMIT :count")
    suspend fun getAfter(channelId: String, count: Int, timestamp: Long): List<Data>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :channelId ORDER BY timetoken ASC LIMIT 1")
    fun getFirst(channelId: String): Flow<Data?>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :channelId ORDER BY timetoken DESC LIMIT 1")
    fun getLast(channelId: String): Flow<Data?>

    @Transaction
    @Query("SELECT * FROM message ORDER BY timetoken DESC")
    fun getAll(): Flow<List<Data>>

    @Transaction
    @Query("SELECT * FROM message WHERE publisher LIKE :userId ORDER BY timetoken DESC")
    fun getAll(userId: String): Flow<List<Data>>

    @Transaction
    @Query("SELECT * FROM message WHERE isSent LIKE 0 ORDER BY timetoken DESC")
    fun getUnsent(): Flow<List<Data>>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :channelId ORDER BY timetoken DESC")
    fun getByChannel(channelId: String): Flow<List<Data>>

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, Data>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :channelId ORDER BY timetoken DESC LIMIT :count")
    fun getLastByChannel(channelId: String, count: Long): Flow<List<Data>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg message: DB)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg message: DB)

    @Delete
    suspend fun delete(messageData: DB)

    @Query("DELETE FROM message WHERE channel LIKE :channelId")
    suspend fun deleteAll(channelId: String)
}
