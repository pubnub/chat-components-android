package com.pubnub.components.data.membership

import androidx.room.Dao

@Dao
interface DefaultMembershipDao : MembershipDao<DBMembership>