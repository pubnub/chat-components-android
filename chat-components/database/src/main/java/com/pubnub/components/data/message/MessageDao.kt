package com.pubnub.components.data.message

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

interface MessageDao<IN : Message, OUT : Message> {

    @Transaction
    @Query("SELECT * FROM message WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: MessageId): OUT?

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id AND timetoken < :timestamp ORDER BY timetoken DESC LIMIT :count")
    suspend fun getBefore(id: ChannelId, count: Int, timestamp: Timetoken): List<OUT>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id AND timetoken > :timestamp ORDER BY timetoken DESC LIMIT :count")
    suspend fun getAfter(id: ChannelId, count: Int, timestamp: Timetoken): List<OUT>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken ASC LIMIT 1")
    fun getFirst(id: ChannelId): Flow<OUT?>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken DESC LIMIT 1")
    fun getLast(id: ChannelId): Flow<OUT?>

    @Transaction
    @Query("SELECT * FROM message ORDER BY timetoken DESC")
    fun getAll(): Flow<List<OUT>>

    @Transaction
    @Query("SELECT * FROM message WHERE publisher LIKE :id ORDER BY timetoken DESC")
    fun getAll(id: UserId): Flow<List<OUT>>

    @Transaction
    @Query("SELECT * FROM message WHERE isSent LIKE 0 ORDER BY timetoken DESC")
    fun getUnsent(): Flow<List<OUT>>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken DESC")
    fun getByChannel(id: ChannelId): Flow<List<OUT>>

    @Transaction
    @RawQuery
    fun getAll(query: SupportSQLiteQuery): PagingSource<Int, OUT>

    @Transaction
    @Query("SELECT * FROM message WHERE channel LIKE :id ORDER BY timetoken DESC LIMIT :count")
    fun getLastByChannel(id: ChannelId, count: Long): Flow<List<OUT>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(vararg message: IN)

    @Delete
    suspend fun delete(messageData: IN)

    @Query("DELETE FROM message WHERE channel LIKE :id")
    suspend fun deleteAll(id: ChannelId)
}
