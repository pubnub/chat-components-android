package com.pubnub.components.chat.ui.mapper.message

import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.message.Attachment
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.components.data.message.DBAttachment
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import timber.log.Timber

class DBMessageMapper(
    val memberFormatter: (UserId) -> MemberUi.Data,
) : Mapper<DBMessageWithActions, MessageUi.Data> {
    @Suppress("UNCHECKED_CAST")
    override fun map(input: DBMessageWithActions): MessageUi.Data =
        MessageUi.Data(
            uuid = input.id,
            publisher = memberFormatter(input.publisher),
            channel = input.channel,
            type = input.message.type,
            text = input.message.text,
            attachment = (input.attachment as? List<DBAttachment>?)?.mapNotNull { it.toUi() }
                ?: emptyList(),
            timetoken = input.timetoken,
            isSending = !input.isSent,
            isDelivered = !input.isSent,
            reactions = input.actions.toUi(),
        )

    private fun List<DBMessageAction>.toUi(): List<ReactionUi> =
        this.groupBy { it.value }
            .map { (action, list) ->
                ReactionUi(
                    action,
                    list.first().type,
                    list.map { memberFormatter(it.user) })
            }

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
