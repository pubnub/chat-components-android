package com.pubnub.components.chat.ui.mapper.message

import com.pubnub.components.chat.ui.component.message.Attachment
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.data.message.DBAttachment
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.framework.mapper.Mapper

class DomainMessageMapper : Mapper<MessageUi.Data, DBMessageWithActions> {
    override fun map(input: MessageUi.Data): DBMessageWithActions =

        DBMessageWithActions(
            DBMessage(
                id = input.uuid,
                type = input.type,
                text = input.text,
                attachment = input.attachment.toDb(),
                custom = null,

                publisher = input.publisher.id,
                channel = input.channel,
                timetoken = input.timetoken,
                isSent = !input.isSending && input.isDelivered,
                exception = null,
            ),
            actions = emptyList(),
        )

    fun List<Attachment>?.toDb(): List<DBAttachment>? =
        this?.map { it.toDb() }

    fun Attachment.toDb(): DBAttachment =
        when (this) {
            is Attachment.Image -> DBAttachment.Image(imageUrl = imageUrl, custom = custom)
            is Attachment.Link -> DBAttachment.Link(link = link, custom = custom)
//            else -> DBAttachment.Custom(custom = custom)
        }
}
