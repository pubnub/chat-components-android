package com.pubnub.components.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.MessageDao
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
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
    private val messageDao: MessageDao<DBMessage, DBMessage>,
) : MessageRepository<DBMessage, DBMessage> {

    /**
     * Returns Message with Channel list
     *
     * @param messageId Message ID to match
     * @return DBMessage if exists, null otherwise
     */
    override suspend fun get(messageId: String): DBMessage? =
        messageDao.get(messageId)

    /**
     * Returns list of Messages with Channels
     *
     * @param channelId ID of Channel to get Message from
     * @param count Count of Messages to return
     * @param before If true, returns the Messages before passed Timestamp. Otherwise returns messages after Timestamp
     * @param timestamp Timestamp to get Messages before or after
     * @return List of DBMessage
     */
    override suspend fun get(
        channelId: String,
        count: Int,
        before: Boolean,
        timestamp: Long,
    ): List<DBMessage> =
        if (before) messageDao.getBefore(
            channelId = channelId,
            count = count,
            timestamp = timestamp
        )
        else messageDao.getAfter(channelId = channelId, count = count, timestamp = timestamp)

    /**
     * Checks if database contains Message with passed id
     *
     * @param messageId ID of Message to check
     *
     * @return True if Message exists, False otherwise
     */
    override suspend fun has(messageId: String): Boolean =
        get(messageId) != null

    /**
     * Returns Paginated Source of Message list
     *
     * @param channelId When not null, returned list contains only Messages from passed channel.
     *              Otherwise all the Messages are returned.
     * @param filter Room filter query
     * @param sorted Array of Sorted objects, result will be sorted by it.
     * @return PagingSource of DBChannelWithMembers
     */
    override fun getAll(
        channelId: String?,
        filter: Query?,
        vararg sorted: Sorted,
    ): PagingSource<Int, DBMessage> {
        val stringQuery: MutableList<String> = mutableListOf("SELECT * FROM `message`")
        val arguments: MutableList<Any> = mutableListOf()

        // Where clause
        if (filter != null || channelId != null)
            stringQuery += "WHERE"

        // Filter by userId
        if (channelId != null) {
            stringQuery += "channel LIKE ?"
            arguments.add(channelId)
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
     * @param channelId ID of Channel to get last Message from
     * @return DBMessage or null
     */
    override suspend fun getLast(channelId: String): DBMessage? =
        messageDao.getLast(channelId).firstOrNull()

    /**
     * Returns list of last Messages for passed Channel ID
     *
     * @param channelId ID of Channel to get last Message from
     * @param count Count of Messages to return
     * @return Flow of List<DBMessage>
     */
    override fun getLastByChannel(
        channelId: ChannelId,
        count: Long,
    ): Flow<List<DBMessage>> =
        messageDao.getLastByChannel(channelId, count)

    /**
     * Checks if there is more Messages before passed Timestamp
     *
     * @param channelId ID of Channel to check Messages from
     * @param timestamp Timestamp to check
     * @return True if there are Messages before passed Timestamp, False otherwise
     */
    override suspend fun hasMoreBefore(channelId: String, timestamp: Timetoken): Boolean =
        messageDao.getBefore(channelId, 1, timestamp).isNotEmpty()

    /**
     * Checks if there is more Messages after passed Timestamp
     *
     * @param channelId ID of Channel to check Messages from
     * @param timestamp Timestamp to check
     * @return True if there are Messages after passed Timestamp, False otherwise
     */
    override suspend fun hasMoreAfter(channelId: String, timestamp: Timetoken): Boolean =
        messageDao.getAfter(channelId, 1, timestamp).isNotEmpty()

    /**
     * Adds passed Message to database
     *
     * @param message DBMessage object do add
     */
    override suspend fun add(message: DBMessage) {
        messageDao.insert(message)
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
     * @param channel ID of Channel to remove Messages from
     */
    override suspend fun removeAll(channel: ChannelId) {
        messageDao.deleteAll(channel)
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
     * Sets status of Message
     *
     * @param messageId Id of Message to set status to
     * @param isSent True if Message is sent, False otherwise
     * @param exception Exception of sending Message, or null
     * @param timestamp Timestamp of update, or null
     */
    override suspend fun setStatus(
        messageId: String,
        isSent: Boolean,
        exception: String?,
        timestamp: Timetoken?,
    ) {
        val message = get(messageId) ?: throw Exception("Message not found exception")
        val updatedMessage = message.copy(
            isSent = isSent,
            exception = exception,
            timetoken = timestamp ?: message.timetoken,
        )
        messageDao.update(updatedMessage)
    }

    /**
     * Sets SENT status to Message
     *
     * @param messageId Id of Message to set status to
     * @param timestamp Timestamp of update, or null
     */
    override suspend fun setSent(messageId: String, timestamp: Timetoken?) {
        setStatus(
            messageId = messageId,
            isSent = true,
            exception = null,
            timestamp = timestamp,
        )
    }

    /**
     * Sets ERROR status to Message
     *
     * @param messageId Id of Message to set status to
     * @param exception Exception of sending Message, or null
     * @param timestamp Timestamp of update, or null
     */
    override suspend fun setSendingError(
        messageId: String,
        exception: String?,
        timestamp: Timetoken?,
    ) {
        setStatus(
            messageId = messageId,
            isSent = false,
            exception = exception,
            timestamp = timestamp,
        )
    }

    /**
     * Returns last message Timestamp
     *
     * @param channelId ID of Channel to get Messages from
     * @return last Message Timestamp or 1, when not exists
     */
    override suspend fun getLastTimestamp(channelId: String): Long =
        messageDao.getLast(channelId).firstOrNull()?.timetoken ?: 1L
}
