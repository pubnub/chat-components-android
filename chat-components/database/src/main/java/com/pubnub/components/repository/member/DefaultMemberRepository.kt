package com.pubnub.components.repository.member

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.data.member.MemberDao
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

/**
 * Member Repository Implementation
 *
 * Parameters:
 * @param memberDao MemberDao implementation
 */
class DefaultMemberRepository(
    private val memberDao: MemberDao<DBMember, DBMemberWithChannels>,
) : MemberRepository<DBMember, DBMemberWithChannels> {

    /**
     * Returns Member with Channel list
     *
     * @param id User ID to match
     * @return DBMemberWithChannels if exists, null otherwise
     */
    override suspend fun get(id: UserId): DBMemberWithChannels? =
        memberDao.get(id)

    /**
     * Returns Paginated Source of Members with Channel list
     *
     * @param id When not null, returns list of Members for selected channel.
     *              Otherwise list of all Members will be returned.
     * @param filter Room filter query
     * @param sorted Array of Sorted objects, result will be sorted by it.
     * @return PagingSource of DBMemberWithChannels
     */
    override fun getAll(
        id: ChannelId?,
        filter: Query?,
        vararg sorted: Sorted,
    ): PagingSource<Int, DBMemberWithChannels> {
        val stringQuery: MutableList<String> = mutableListOf("SELECT * FROM `member`")
        val arguments: MutableList<Any> = mutableListOf()

        // Where clause
        if (filter != null || id != null)
            stringQuery += "WHERE"

        // Filter by channelId
        if (id != null) {
            stringQuery += "memberId IN (SELECT memberId FROM `membership` WHERE channelId LIKE ?)"
            arguments.add(id)
        }

        // Filtering
        if (filter != null) {
            if (id != null)
                stringQuery += " AND "
            stringQuery += filter.first
            arguments.addAll(filter.second)
        }

        // Ordering
        if (sorted.isNotEmpty())
            stringQuery += "ORDER BY ${sorted.joinToString(", ")}"

        val query = SimpleSQLiteQuery(stringQuery.joinToString(" "), arguments.toTypedArray())

        return memberDao.getAll(query)
    }

    /**
     * Returns complete list of Members
     *
     * @param id When not null, returns list of Members from passed channelId.
     *                  Otherwise list of all Members will be returned.
     * @return List of DBMemberWithChannels
     */
    override suspend fun getList(id: ChannelId?): List<DBMemberWithChannels> =
        id?.let { memberDao.getList(it) } ?: memberDao.getList()

    /**
     * Inserts passed Members to database, or updates them if they exists
     *
     * @param member DBMember object to add
     */
    override suspend fun insertOrUpdate(vararg member: DBMember) {
        memberDao.insertOrUpdate(*member)
    }

    /**
     * Removes Member with passed id
     *
     * @param id ID of User to remove
     */
    override suspend fun remove(id: UserId) {
        memberDao.delete(id)
    }

    /**
     * Returns count of all Members
     *
     * @return Count of all Members
     */
    override suspend fun size(): Long =
        memberDao.size()
}
