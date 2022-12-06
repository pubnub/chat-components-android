package com.pubnub.components.data.message

import androidx.annotation.Keep
import androidx.room.*
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken
import com.pubnub.framework.util.toIsoString
import com.pubnub.framework.util.toTimetoken

@Keep
@Entity(
    tableName = "message",
    indices = [Index("publisher")]
)
data class DBMessage(
    @PrimaryKey
    override val id: String,
    override val text: String,
    override val contentType: String? = null,
    @field:TypeConverters(CustomContentConverter::class)
    override val content: Any? = null,
    @ColumnInfo(defaultValue = "") // todo: how to handle Iso date in compile time?
    override val createdAt: String = System.currentTimeMillis().timetoken.toIsoString(),
    @field:TypeConverters(CustomContentConverter::class)
    override val custom: Any? = null,
    val publisher: UserId,
    val channel: ChannelId,
    val timetoken: Timetoken = createdAt.toTimetoken(),
    @ColumnInfo(defaultValue = "0")
    val published: Timetoken,
    val isSent: Boolean = true,
    var exception: String? = null,
) : Message
