package com.pubnub.components.repository.sync

import com.pubnub.components.data.sync.DBRemoteTimetoken
import com.pubnub.components.data.sync.RemoteTimetokenDao
import com.pubnub.framework.data.ChannelId

/**
 * Remote Timetoken Repository Implementation
 *
 * Parameters:
 * @param remoteTimetokenDao RemoteKey DaO implementation
 */
class DefaultRemoteTimetokenRepository(
    private val remoteTimetokenDao: RemoteTimetokenDao<DBRemoteTimetoken>,
) : RemoteTimetokenRepository<DBRemoteTimetoken> {

    /**
     * Returns a remote timetoken
     *
     * @param table Name of the table
     * @return DBRemoteKey or null
     */
    override suspend fun get(table: String, channelId: ChannelId): DBRemoteTimetoken? =
        remoteTimetokenDao.get(table, channelId)

    /**
     * Inserts passed RemoteTimetoken to database, or updates them if they exists
     *
     * @param data RemoteTimetoken object to add, containing time window needed to sync
     */
    override suspend fun insertOrUpdate(vararg data: DBRemoteTimetoken) {
        remoteTimetokenDao.insertOrUpdate(*data)
    }

    /**
     * Removes passed RemoteTimetoken
     *
     * @param data RemoteTimetoken object to remove
     */
    override suspend fun remove(data: DBRemoteTimetoken) {
        remoteTimetokenDao.delete(data)
    }

    /**
     * Returns count of all RemoteTimetoken
     *
     * @return Count of all RemoteTimetoken
     */
    override suspend fun size(): Long =
        remoteTimetokenDao.size()

    /**
     * Returns count of RemoteTimetoken from passed table
     *
     * @return Count of RemoteTimetoken from passed table
     */
    override suspend fun size(table: String, channelId: ChannelId): Long =
        remoteTimetokenDao.size(table, channelId)
}
