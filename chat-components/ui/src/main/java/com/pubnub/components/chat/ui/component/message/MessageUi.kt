package com.pubnub.components.chat.ui.component.message

import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.util.Timetoken

sealed class MessageUi {
    data class Data(
        val uuid: MessageId,
        val publisher: MemberUi.Data,
        val channel: ChannelId,
        val type: String = "default",
        val text: String?,
        val attachment: List<Attachment> = emptyList(),
        val timetoken: Timetoken,
        val isSending: Boolean,
        val isDelivered: Boolean,
    ) : MessageUi()

    data class Separator(val text: String) : MessageUi()
}

val List<Attachment>.images
    get() = this.filterIsInstance<Attachment.Image>()

val List<Attachment>.links
    get() = this.filterIsInstance<Attachment.Link>()

sealed class Attachment(open val type: String, open val custom: Any? = null) {
    data class Image(
        val imageUrl: String,
    ) : Attachment("image")

    data class Link(
        val link: String,
    ) : Attachment("link")
}