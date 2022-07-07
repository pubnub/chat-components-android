package com.pubnub.components.chat.ui.mapper.typing

import com.pubnub.components.chat.ui.component.input.TypingUi
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.framework.data.Typing
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper

class DomainTypingMapper(
    private val memberFormatter: (UserId) -> MemberUi.Data,
) : Mapper<Typing, TypingUi> {
    override fun map(input: Typing): TypingUi =
        TypingUi(
            user = memberFormatter(input.userId),
            isTyping = input.isTyping,
            timestamp = input.timestamp,
        )
}
