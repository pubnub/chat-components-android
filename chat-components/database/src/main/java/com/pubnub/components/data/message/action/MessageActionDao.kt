package com.pubnub.components.data.message.action

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageActionDao<Data : MessageAction> {

    @Query("SELECT * FROM message_action WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: String): Data?

    @Query("SELECT * FROM message_action WHERE messageTimestamp LIKE :messageTimestamp")
    fun getByMessage(messageTimestamp: Long): Flow<List<Data>>

    @Query("SELECT * FROM message_action ORDER BY messageTimestamp DESC LIMIT 1")
    fun getLast(): Flow<Data?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg reaction: Data)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg reaction: Data)

    @Delete
    suspend fun delete(vararg reaction: Data)
}
