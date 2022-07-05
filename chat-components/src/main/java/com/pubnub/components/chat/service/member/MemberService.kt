package com.pubnub.components.chat.service.member

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.api.models.consumer.objects.PNKey
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.api.models.consumer.objects.PNSortKey
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.Member
import com.pubnub.framework.data.UserId

interface MemberService<Data : Member> {
    fun bind(vararg channels: String)
    fun unbind()
    fun fetch(
        id: UserId,
        includeCustom: Boolean = false,
    )

    fun fetchAll(
        limit: Int? = null,
        page: PNPage? = null,
        filter: String? = null,
        sort: Collection<PNSortKey<PNKey>> = listOf(),
        includeCustom: Boolean = true,
    )

    fun add(member: Data, includeCustom: Boolean)
    fun remove(id: UserId? = null)
}

val LocalMemberService =
    staticCompositionLocalOf<MemberService<DBMember>> { throw MemberServiceNotInitializedException() }

class MemberServiceNotInitializedException :
    Exception("Channel Service not initialized")