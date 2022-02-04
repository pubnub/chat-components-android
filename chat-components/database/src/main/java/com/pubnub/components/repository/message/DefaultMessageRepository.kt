package com.pubnub.components.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.MessageDao
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Message Repository Implementation
 *
 * Parameters:
 * @param messageDao MessageDao implementation
 */
@OptIn(ExperimentalPagingApi::class)
class DefaultMessageRepository(
    private val messageDao: MessageDao<DBMessage, DBMessageWithActions>,
) : MessageRepository<DBMessage, DBMessageWithActions> {

    /**
     * Returns Message with Channel list
     *
     * @param id Message ID to match
     * @return DBMessageWithActions if exists, null otherwise
     */
    override suspend fun get(id: MessageId): DBMessageWithActions? =
        messageDao.get(id)

    /**
     * Returns list of Messages with Channels
     *
     * @param id ID of Channel to get Message from
     * @param count Count of Messages to return
     * @param before If true, returns the Messages before passed Timestamp. Otherwise returns messages after Timestamp
     * @param timestamp Timestamp to get Messages before or after
     * @return List of DBMessageWithActions
     */
    override suspend fun getList(
        id: ChannelId,
        count: Int,
        before: Boolean,
        timestamp: Timetoken,
    ): List<DBMessageWithActions> =
        if (before) messageDao.getBefore(
            id = id,
            count = count,
            timestamp = timestamp
        )
        else messageDao.getAfter(id = id, count = count, timestamp = timestamp)

    /**
     * Checks if database contains Message with passed id
     *
     * @param id ID of Message to check
     *
     * @return True if Message exists, False otherwise
     */
    override suspend fun has(id: MessageId): Boolean =
        get(id) != null

    /**
     * Returns Paginated Source of Message list
     *
     * @param id When not null, returned list contains only Messages from passed channel.
     *              Otherwise all the Messages are returned.
     * @param filter Room filter query
     * @param sorted Array of Sorted objects, result will be sorted by it.
     * @return PagingSource of DBChannelWithMembers
     */
    override fun getAll(
        id: ChannelId?,
        filter: Query?,
        vararg sorted: Sorted,
    ): PagingSource<Int, DBMessageWithActions> {
        val stringQuery: MutableList<String> = mutableListOf("SELECT * FROM `message`")
        val arguments: MutableList<Any> = mutableListOf()

        // Where clause
        if (filter != null || id != null)
            stringQuery += "WHERE"

        // Filter by userId
        if (id != null) {
            stringQuery += "channel LIKE ?"
            arguments.add(id)
        }

        // Filtering
        if (filter != null) {
            stringQuery += filter.first
            arguments.addAll(filter.second)
        }

        // Ordering
        if (sorted.isNotEmpty())
            stringQuery += "ORDER BY ${sorted.joinToString(", ")}"

        val query = SimpleSQLiteQuery(stringQuery.joinToString(" "), arguments.toTypedArray())

        return messageDao.getAll(query)
    }

    /**
     * Returns last Message for passed Channel ID
     *
     * @param id ID of Channel to get last Message from
     * @return DBMessageWithActions or null
     */
    override suspend fun getLast(id: ChannelId): DBMessageWithActions? =
        messageDao.getLast(id).firstOrNull()

    /**
     * Returns list of last Messages for passed Channel ID
     *
     * @param id ID of Channel to get last Message from
     * @param count Count of Messages to return
     * @return Flow of List<DBMessageWithActions>
     */
    override fun getLastByChannel(
        id: ChannelId,
        count: Long,
    ): Flow<List<DBMessageWithActions>> =
        messageDao.getLastByChannel(id, count)

    /**
     * Checks if there is more Messages before passed Timestamp
     *
     * @param id ID of Channel to check Messages from
     * @param timestamp Timestamp to check
     * @return True if there are Messages before passed Timestamp, False otherwise
     */
    override suspend fun hasMoreBefore(id: ChannelId, timestamp: Timetoken): Boolean =
        messageDao.getBefore(id, 1, timestamp).isNotEmpty()

    /**
     * Checks if there is more Messages after passed Timestamp
     *
     * @param id ID of Channel to check Messages from
     * @param timestamp Timestamp to check
     * @return True if there are Messages after passed Timestamp, False otherwise
     */
    override suspend fun hasMoreAfter(id: ChannelId, timestamp: Timetoken): Boolean =
        messageDao.getAfter(id, 1, timestamp).isNotEmpty()

    /**
     * Adds passed Message to database
     *
     * @param message DBMessage object do add
     */
    override suspend fun add(vararg message: DBMessage) {
        messageDao.insert(*message)
    }

    /**
     * Removes passed Message from database
     *
     * @param message DBMessage object do remove
     */
    override suspend fun remove(message: DBMessage) {
        messageDao.delete(message)
    }

    /**
     * Removes all Messages from passed Channel
     *
     * @param id ID of Channel to remove Messages from
     */
    override suspend fun removeAll(id: ChannelId) {
        messageDao.deleteAll(id)
    }

    /**
     * Updates passed Message
     *
     * @param message DBMessage object to update
     */
    override suspend fun update(message: DBMessage) {
        messageDao.update(message)
    }

    /**
     * Sets SENT status to Message
     *
     * @param id Id of Message to set status to
     * @param timestamp Timestamp of update, or null
     */
    override suspend fun setSent(id: MessageId, timestamp: Timetoken?) {
        setStatus(
            id = id,
            isSent = true,
            exception = null,
            timestamp = timestamp,
        )
    }

    /**
     * Sets ERROR status to Message
     *
     * @param id Id of Message to set status to
     * @param exception Exception of sending Message, or null
     * @param timestamp Timestamp of update, or null
     */
    override suspend fun setSendingError(
        id: MessageId,
        exception: String?,
        timestamp: Timetoken?,
    ) {
        setStatus(
            id = id,
            isSent = false,
            exception = exception,
            timestamp = timestamp,
        )
    }

    /**
     * Returns last message Timestamp
     *
     * @param id ID of Channel to get Messages from
     * @return last Message Timestamp or 1, when not exists
     */
    override suspend fun getLastTimestamp(id: ChannelId): Timetoken =
        messageDao.getLast(id).firstOrNull()?.timetoken ?: 1L

    /**
     * Sets status of Message
     *
     * @param id Id of Message to set status to
     * @param isSent True if Message is sent, False otherwise
     * @param exception Exception of sending Message, or null
     * @param timestamp Timestamp of update, or null
     */
    private suspend fun setStatus(
        id: MessageId,
        isSent: Boolean,
        exception: String?,
        timestamp: Timetoken?,
    ) {
        val message = get(id) ?: throw Exception("Message not found exception")
        val updatedMessage = message.message.copy(
            isSent = isSent,
            exception = exception,
            timetoken = timestamp ?: message.timetoken,
        )
        messageDao.update(updatedMessage)
    }
}
