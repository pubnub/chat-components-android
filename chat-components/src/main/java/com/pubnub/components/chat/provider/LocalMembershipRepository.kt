package com.pubnub.components.chat.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.repository.membership.MembershipRepository

val LocalMembershipRepository =
    compositionLocalOf<MembershipRepository<DBMembership>> { throw MembershipRepositoryNotInitializedException() }

class MembershipRepositoryNotInitializedException :
    Exception("Membership repository not initialized")
