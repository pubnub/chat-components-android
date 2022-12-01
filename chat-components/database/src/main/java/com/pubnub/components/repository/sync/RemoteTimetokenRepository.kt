package com.pubnub.components.repository.sync

import com.pubnub.components.data.sync.RemoteTimetoken
import com.pubnub.framework.data.ChannelId

interface RemoteTimetokenRepository<DB : RemoteTimetoken> {
    suspend fun get(table: String, channelId: ChannelId): DB?
    suspend fun insertOrUpdate(vararg data: DB)
    suspend fun remove(data: DB)
    suspend fun size(): Long
    suspend fun size(table: String, channelId: ChannelId): Long
}
