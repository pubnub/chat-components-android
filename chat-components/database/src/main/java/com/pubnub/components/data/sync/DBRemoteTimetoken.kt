package com.pubnub.components.data.sync

import android.util.Range
import androidx.room.Entity
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken

@Entity(
    tableName = "remote_timetoken",
    primaryKeys = ["table", "channelId"]
)
data class DBRemoteTimetoken(
    override val table: String,
    override val channelId: ChannelId,
    override val start: Timetoken, // min
    override val end: Timetoken, // max
) : RemoteTimetoken {

    fun getRange() = Range(start, end)

    operator fun plus(newRemoteRange: DBRemoteTimetoken): DBRemoteTimetoken {
        assert(table == newRemoteRange.table) { "Cannot extend a range for different table names" }
        assert(channelId == newRemoteRange.channelId) { "Cannot extend a range for different channels" }

        val newRange = getRange().extend(newRemoteRange.getRange())
        return copy(start = newRange.lower, end = newRange.upper)
    }
}
