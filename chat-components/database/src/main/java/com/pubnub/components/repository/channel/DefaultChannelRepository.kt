package com.pubnub.components.repository.channel

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.pubnub.components.data.channel.ChannelDao
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

/**
 * Channel Repository Implementation
 *
 * Parameters:
 * @param channelDao ChannelDao implementation
 */
class DefaultChannelRepository(
    private val channelDao: ChannelDao<DBChannel, DBChannelWithMembers>,
) : ChannelRepository<DBChannel, DBChannelWithMembers> {

    /**
     * Returns Channel with Member list
     *
     * @param channelId Channel ID to match
     * @return DBChannelWithMembers if exists, null otherwise
     */
    override suspend fun get(channelId: ChannelId): DBChannelWithMembers? =
        channelDao.get(channelId)

    /**
     * Returns Paginated Source of Channels with Member list
     *
     * @param userId When not null, returned list contains only joined channels by user.
     *              Otherwise all the Channels are returned.
     * @param filter Room filter query
     * @param sorted Array of Sorted objects, result will be sorted by it.
     * @return PagingSource of DBChannelWithMembers
     */
    override fun getAll(
        userId: UserId?,
        filter: Query?,
        vararg sorted: Sorted
    ): PagingSource<Int, DBChannelWithMembers> {
        val stringQuery: MutableList<String> = mutableListOf("SELECT * FROM `channel`")
        val arguments: MutableList<Any> = mutableListOf()

        // Where clause
        if (filter != null || userId != null)
            stringQuery += "WHERE"

        // Filter by userId
        if (userId != null) {
            stringQuery += "channelId IN (SELECT `channelId` FROM `membership` WHERE memberId LIKE ?)"
            arguments.add(userId)
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

        return channelDao.getAll(query)
    }

    /**
     * Returns complete list of Channels
     *
     * @return List of DBChannelWithMembers
     */
    override suspend fun getList(): List<DBChannelWithMembers> =
        channelDao.getList()

    /**
     * Adds passed Channels to database
     *
     * @param channel DBChannel object do add
     */
    override suspend fun add(
        vararg channel: DBChannel,
    ) {
        channelDao.insert(*channel)
    }

    /**
     * Removes Channel with passed channel id
     *
     * @param channelId ID of Channel to remove
     */
    override suspend fun remove(channelId: ChannelId) {
        channelDao.delete(channelId)
    }

    /**
     * Returns count of all Channels
     *
     * @return Count of all Channels
     */
    override suspend fun size(): Long =
        channelDao.size()
}
