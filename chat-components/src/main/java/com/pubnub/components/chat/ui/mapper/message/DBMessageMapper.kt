package com.pubnub.components.chat.ui.mapper.message

import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.message.Attachment
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.data.message.DBAttachment
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import timber.log.Timber

class DBMessageMapper(
    val memberFormatter: (UserId) -> MemberUi.Data,
) : Mapper<DBMessage, MessageUi.Data> {
    @Suppress("UNCHECKED_CAST")
    override fun map(input: DBMessage): MessageUi.Data =
        MessageUi.Data(
            uuid = input.id,
            publisher = memberFormatter(input.publisher),
            channel = input.channel,
            type = input.type,
            text = input.text,
            attachment = input.attachment?.mapNotNull { it.toUi() }
                ?: emptyList(),
            timetoken = input.timetoken,
            isSending = !input.isSent,
            isDelivered = !input.isSent,
        )

    private fun DBAttachment.toUi(): Attachment? = try {
        when (this) {
            is DBAttachment.Image -> {
                Attachment.Image(imageUrl = imageUrl)
            }
            is DBAttachment.Link -> {
                Attachment.Link(link = link)
            }
            else -> null
        }
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}
