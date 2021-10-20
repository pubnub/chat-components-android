package com.pubnub.components.repository.membership

import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.data.membership.MembershipDao
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

/**
 * Membership Repository Implementation
 *
 * Parameters:
 * @param membershipDao MembershipDao implementation
 */
class DefaultMembershipRepository(
    private val membershipDao: MembershipDao<DBMembership>,
) : MembershipRepository<DBMembership> {

    /**
     * Returns Membership matches passed userId
     *
     * @param userId User ID to match
     * @return DBMembership if exists, null otherwise
     */
    override suspend fun get(userId: UserId): DBMembership? =
        membershipDao.get(userId)

    /**
     * Returns Flowable list of memberships
     *
     * @param userId ID of User to get Membership from
     * @return Flow from List of DBMembership
     */
    override fun getAll(userId: UserId): Flow<List<DBMembership>> =
        membershipDao.getAll(userId)

    /**
     * Returns List of memberships from all Users
     *
     * @return List of DBMembership
     */
    override suspend fun getList(): List<DBMembership> =
        membershipDao.getList()

    /**
     * Adds passed Membership to database
     *
     * @param membership Membership object to add, connecting the User to the Channel
     */
    override suspend fun add(vararg membership: DBMembership) {
        membershipDao.insert(*membership)
    }

    /**
     * Removes Membership with passed id
     *
     * @param id ID of Membership to remove
     */
    override suspend fun remove(id: String) {
        membershipDao.delete(id)
    }


    /**
     * Returns count of all Memberships
     *
     * @return Count of all Memberships
     */
    override suspend fun size(): Long =
        membershipDao.size()
}
