package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.repository.membership.DefaultMembershipRepository

val LocalMembershipRepository =
    staticCompositionLocalOf<DefaultMembershipRepository> { throw MembershipRepositoryNotInitializedException() }

class MembershipRepositoryNotInitializedException :
    Exception("Membership repository not initialized")
