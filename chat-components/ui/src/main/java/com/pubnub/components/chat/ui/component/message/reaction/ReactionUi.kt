package com.pubnub.components.chat.ui.component.message.reaction

import com.pubnub.components.chat.ui.component.member.MemberUi

data class ReactionUi(
    val reaction: String,
    val type: String,
    val members: List<MemberUi.Data>,
)
