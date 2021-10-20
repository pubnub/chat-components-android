package com.pubnub.components.chat.ui.component.member

import com.pubnub.framework.data.UserId

sealed class MemberUi {
    data class Data(
        val id: UserId,
        val name: String,
        val profileUrl: String?,
        val description: String,
//        val channels: List<ChannelUi.Data>
    ) : MemberUi()

    data class Separator(val text: String) : MemberUi()
}