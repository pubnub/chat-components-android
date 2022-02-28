package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.repository.membership.MembershipRepository

val LocalMembershipRepository =
    staticCompositionLocalOf<MembershipRepository<DBMembership>> { throw MembershipRepositoryNotInitializedException() }

class MembershipRepositoryNotInitializedException :
    Exception("Membership repository not initialized")
