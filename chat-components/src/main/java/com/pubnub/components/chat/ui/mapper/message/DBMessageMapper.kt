package com.pubnub.components.chat.ui.mapper.message

import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper

class DBMessageMapper(
    private val memberFormatter: (UserId) -> MemberUi.Data,
) : Mapper<DBMessageWithActions, MessageUi.Data> {
    @Suppress("UNCHECKED_CAST")
    override fun map(input: DBMessageWithActions): MessageUi.Data =
        MessageUi.Data(
            uuid = input.id,
            publisher = memberFormatter(input.message.publisher),
            channel = input.message.channel,
            text = input.message.text,
            createdAt = input.createdAt,
            timetoken = input.message.timetoken,
            isSending = !input.message.isSent,
            isDelivered = !input.message.isSent,
            reactions = input.actions.toUi(),
            contentType = input.message.contentType ?: "default",
            content = input.message.content,
            custom = input.custom,
        )

    private fun List<DBMessageAction>.toUi(): List<ReactionUi> =
        this.groupBy { it.value }
            .map { (action, list) ->
                ReactionUi(
                    action,
                    list.first().type,
                    list.map { memberFormatter(it.user) })
            }
}
