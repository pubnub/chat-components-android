package com.pubnub.components.data.message

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pubnub.components.data.member.DBMember
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken

@Keep
@Entity(
    tableName = "message",
    indices = [Index("publisher")]
)
data class DBMessage(
    @PrimaryKey
    override val id: String,
    override val type: String,
    override val text: String? = null,
    override val attachment: List<DBAttachment>? = null,
    override val custom: Map<String, Any>? = null,
    override val publisher: UserId,
    override val channel: ChannelId,
    override val timetoken: Timetoken = System.currentTimeMillis().timetoken,
    override val isSent: Boolean = true,
    override var exception: String? = null,
) : Message

@Keep
abstract class DBAttachment private constructor() : Attachment {
    @Keep
    data class Image(
        val imageUrl: String,
        override val type: String = "image",
        override val custom: Any? = null
    ) : DBAttachment()

    @Keep
    data class Link(
        val link: String,
        override val type: String = "link",
        override val custom: Any? = null
    ) : DBAttachment()

    @Keep
    data class Custom(
        override val type: String = "custom",
        override val custom: Any?
    ) : DBAttachment()

}
