package com.pubnub.components.data.message.action

import androidx.room.*
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageActionDao<Data : MessageAction> {

    @Query("SELECT * FROM message_action WHERE id LIKE :id LIMIT 1")
    suspend fun get(id: String): Data?

    @Query("SELECT * FROM message_action WHERE user LIKE :user AND channel LIKE :channel AND type LIKE :type AND `value` LIKE :value AND messageTimestamp LIKE :messageTimetoken LIMIT 1")
    suspend fun get(
        user: UserId,
        channel: ChannelId,
        messageTimetoken: Timetoken,
        type: String,
        value: String,
    ): Data?

    @Query("SELECT * FROM message_action WHERE messageTimestamp LIKE :messageTimestamp")
    fun getByMessage(messageTimestamp: Long): Flow<List<Data>>

    @Query("SELECT * FROM message_action ORDER BY messageTimestamp DESC LIMIT 1")
    fun getLast(): Flow<Data?>

    @Query("SELECT * FROM message_action WHERE channel LIKE :channel ORDER BY published DESC LIMIT 1")
    fun getLast(channel: String): Flow<Data?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOrUpdate(vararg action: Data)

    @Delete
    suspend fun delete(vararg action: Data)
}
