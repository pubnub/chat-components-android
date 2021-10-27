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
    foreignKeys = [
        ForeignKey(
            entity = DBMember::class,
            parentColumns = arrayOf("memberId"),
            childColumns = arrayOf("publisher"),
            onDelete = ForeignKey.NO_ACTION,
        ),
//        Warning: Entity must exists in db! So Channel must exists in db before Message will be saved
//        ForeignKey(
//            entity = DBChannel::class,
//            parentColumns = arrayOf("id"),
//            childColumns = arrayOf("channel"),
//            onDelete = ForeignKey.NO_ACTION,
//        ),
    ],
    indices = [Index("publisher")]
)
data class DBMessage(
    @PrimaryKey
    override val id: String,
    override val type: String,
    override val text: String?,
    override val attachment: List<DBAttachment>?,
    override val custom: Map<String, Any>?,
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