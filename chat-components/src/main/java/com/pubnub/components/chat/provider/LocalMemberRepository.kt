package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.repository.member.DefaultMemberRepository

val LocalMemberRepository =
    staticCompositionLocalOf<DefaultMemberRepository> { throw MemberRepositoryNotInitializedException() }

class MemberRepositoryNotInitializedException : Exception("Member repository not initialized")
