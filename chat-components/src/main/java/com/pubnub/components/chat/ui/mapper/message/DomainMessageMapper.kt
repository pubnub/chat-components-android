package com.pubnub.components.chat.ui.mapper.message

import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.toTimetoken

class DomainMessageMapper : Mapper<MessageUi.Data, DBMessageWithActions> {
    override fun map(input: MessageUi.Data): DBMessageWithActions =
        DBMessageWithActions(
            DBMessage(
                id = input.uuid,
                text = input.text,
                contentType = input.contentType,
                content = input.content,
                createdAt = input.createdAt,
                custom = input.custom,

                publisher = input.publisher.id,
                published = input.published,
                channel = input.channel,
                timetoken = input.createdAt.toTimetoken(),
                isSent = !input.isSending && input.isDelivered,
                exception = null,
            ),
            actions = emptyList(),
        )
}
