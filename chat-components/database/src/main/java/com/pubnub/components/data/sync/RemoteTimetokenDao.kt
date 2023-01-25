package com.pubnub.components.data.sync

import androidx.room.*
import com.pubnub.framework.data.ChannelId

interface RemoteTimetokenDao<Data : RemoteTimetoken> {

    @Transaction
    @Query("SELECT * FROM `remote_timetoken` WHERE `table` LIKE :table and channelId LIKE :channelId LIMIT 1")
    fun get(table: String, channelId: ChannelId): Data?

    @Upsert
    suspend fun insertOrUpdate(vararg data: Data)

    @Delete
    suspend fun delete(data: Data)

    @Query("SELECT COUNT(*) FROM `remote_timetoken`")
    suspend fun size(): Long

    @Query("SELECT COUNT(*) FROM `remote_timetoken` WHERE `table` LIKE :table and channelId LIKE :channelId")
    suspend fun size(table: String, channelId: ChannelId): Long
}
