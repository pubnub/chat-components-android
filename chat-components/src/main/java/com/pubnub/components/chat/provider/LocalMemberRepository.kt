package com.pubnub.components.chat.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.repository.member.MemberRepository

val LocalMemberRepository =
    compositionLocalOf<MemberRepository<DBMember, DBMemberWithChannels>> { throw MemberRepositoryNotInitializedException() }

class MemberRepositoryNotInitializedException : Exception("Member repository not initialized")
