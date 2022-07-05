package com.pubnub.components.data.message

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken
import com.pubnub.framework.util.toIsoString
import com.pubnub.framework.util.toTimetoken
import kotlin.time.Duration.Companion.milliseconds

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
    override val content: DBCustomContent? = null,
    @ColumnInfo(defaultValue = "") // todo: how to handle Iso date in compile time?
    override val createdAt: String = System.currentTimeMillis().timetoken.toIsoString(),
    override val custom: DBCustomContent? = null,
    val publisher: UserId,
    val channel: ChannelId,
    val timetoken: Timetoken = createdAt.toTimetoken(),
    val isSent: Boolean = true,
    var exception: String? = null,
) : Message

typealias DBCustomContent = Map<String, Any?>
