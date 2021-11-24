package com.pubnub.components.data.message

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

interface MessageDao<DB : Message, Data : Message> {

    @Transaction
    @Query("SELECT * FROM message WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: MessageId): Data?

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id AND timetoken < :timestamp ORDER BY timetoken DESC LIMIT :count")
    suspend fun getBefore(id: ChannelId, count: Int, timestamp: Timetoken): List<Data>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id AND timetoken > :timestamp ORDER BY timetoken DESC LIMIT :count")
    suspend fun getAfter(id: ChannelId, count: Int, timestamp: Timetoken): List<Data>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken ASC LIMIT 1")
    fun getFirst(id: ChannelId): Flow<Data?>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken DESC LIMIT 1")
    fun getLast(id: ChannelId): Flow<Data?>

    @Transaction
    @Query("SELECT * FROM message ORDER BY timetoken DESC")
    fun getAll(): Flow<List<Data>>

    @Transaction
    @Query("SELECT * FROM message WHERE publisher LIKE :id ORDER BY timetoken DESC")
    fun getAll(id: UserId): Flow<List<Data>>

    @Transaction
    @Query("SELECT * FROM message WHERE isSent LIKE 0 ORDER BY timetoken DESC")
    fun getUnsent(): Flow<List<Data>>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken DESC")
    fun getByChannel(id: ChannelId): Flow<List<Data>>

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, Data>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken DESC LIMIT :count")
    fun getLastByChannel(id: ChannelId, count: Long): Flow<List<Data>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg message: DB)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg message: DB)

    @Delete
    suspend fun delete(messageData: DB)

    @Query("DELETE FROM message WHERE channel LIKE :id")
    suspend fun deleteAll(id: ChannelId)
}
