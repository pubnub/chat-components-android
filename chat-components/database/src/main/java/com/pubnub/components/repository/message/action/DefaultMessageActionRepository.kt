package com.pubnub.components.repository.message.action

import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.MessageActionDao
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.flow.firstOrNull

/**
 * Message Action Repository Implementation
 *
 * Parameters:
 * @param messageActionDao MessageActionDao implementation
 */
class DefaultMessageActionRepository(
    private val messageActionDao: MessageActionDao<DBMessageAction>,
) : MessageActionRepository<DBMessageAction> {

    /**
     * Returns Message Action for provided parameters
     *
     * @param user User Id to match
     * @param channel Channel ID to match
     * @param messageTimetoken Timetoken of Message
     * @param type Type of MessageAction
     * @param value Value of MessageAction
     * @return DBMessageAction if exists, null otherwise
     */
    override suspend fun get(
        user: UserId,
        channel: ChannelId,
        messageTimetoken: Timetoken,
        type: String,
        value: String,
    ): DBMessageAction? =
        messageActionDao.get(user, channel, messageTimetoken, type, value)

    /**
     * Inserts passed MessageActions to database, or updates them if they exists
     *
     * @param action DBMessageAction object to add
     */
    override suspend fun insertOrUpdate(vararg action: DBMessageAction) {
        messageActionDao.insertOrUpdate(*action)
    }

    /**
     * Removes passed MessageActions from database
     *
     * @param action DBMessageActions to remove
     */
    override suspend fun remove(vararg action: DBMessageAction) {
        messageActionDao.delete(*action)
    }

    /**
     * Returns if database contains MessageAction with passed id
     *
     * @param id MessageAction ID to check
     */
    override suspend fun has(id: String): Boolean =
        messageActionDao.get(id) != null

    /**
     * Returns the last Timestamp of stored MessageAction.
     *
     * @param channel ID of Channel to get Timestamp from
     * @return MessageAction Timestamp, or 0 when null
     */
    override suspend fun getLastTimetoken(channel: ChannelId): Timetoken =
        messageActionDao.getLast(channel).firstOrNull()?.published ?: 0L
}
