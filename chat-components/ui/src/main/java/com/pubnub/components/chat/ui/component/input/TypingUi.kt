package com.pubnub.components.chat.ui.component.input

import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.framework.util.timetoken

data class TypingUi(
    val user: MemberUi.Data,
    val isTyping: Boolean,
    val timestamp: Long = System.currentTimeMillis().timetoken,
)
