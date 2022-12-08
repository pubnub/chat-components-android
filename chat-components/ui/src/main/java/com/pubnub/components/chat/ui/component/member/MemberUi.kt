package com.pubnub.components.chat.ui.component.member

import com.pubnub.framework.data.UserId

interface MemberUi {
    data class Data(
        val id: UserId,
        val name: String?,
        val type: String = "default",
        val email: String? = null,
        val externalId: String? = null,
        val profileUrl: String? = null,
        val description: String? = null,
        val status: String? = null,
        val custom: Any? = null,
    ) : MemberUi

    data class Separator(val text: String) : MemberUi
}