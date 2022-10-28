package com.pubnub.components.chat.ui.component.member

import com.pubnub.framework.data.UserId

sealed class MemberUi {
    data class Data(
        val id: UserId,
        val name: String?,
        val profileUrl: String? = null,
        val description: String? = null,
        val custom: Any? = null,
        val type: String = "default"
        ) : MemberUi()

    data class Separator(val text: String) : MemberUi()
}