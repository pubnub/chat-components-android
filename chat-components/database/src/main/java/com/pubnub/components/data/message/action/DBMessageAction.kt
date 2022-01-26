package com.pubnub.components.data.message.action

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken

@Keep
@Entity(tableName = "message_action")
data class DBMessageAction(

    override val channel: ChannelId,

    override val user: UserId,

    override val messageTimestamp: Timetoken,

    override val actionTimestamp: Timetoken,

    override val type: String,

    override val action: String,

    @PrimaryKey
    override val id: String = "$user-$channel-$messageTimestamp-$actionTimestamp-$type-$action",
) : MessageAction {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DBMessageAction

        if (channel != other.channel) return false
        if (user != other.user) return false
        if (messageTimestamp != other.messageTimestamp) return false
        if (actionTimestamp != other.actionTimestamp) return false
        if (action != other.action) return false
        if (type != other.type) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channel.hashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + messageTimestamp.hashCode()
        result = 31 * result + actionTimestamp.hashCode()
        result = 31 * result + action.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}