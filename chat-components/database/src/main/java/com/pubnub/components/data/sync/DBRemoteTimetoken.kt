package com.pubnub.components.data.sync

import android.util.Log
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

    init {
        assert(start < end) { "Start value should be smaller than end. $start >= $end" }
    }

    operator fun plus(newRemoteRange: DBRemoteTimetoken): DBRemoteTimetoken {
        assert(table == newRemoteRange.table) { "Cannot extend a range for different table names" }
        assert(channelId == newRemoteRange.channelId) { "Cannot extend a range for different channels" }
        assert(newRemoteRange.start < newRemoteRange.end) { "Start value should be smaller than end" }

        return if (start == newRemoteRange.end) { // prepend
            copy(start = newRemoteRange.start)
        } else if (end == newRemoteRange.start) { // append
            copy(end = newRemoteRange.end)
        } else {
            Log.w(
                "DBRemoteTimetoken",
                "Invalid range extension: [$start, $end] + [${newRemoteRange.start}, ${newRemoteRange.end}]"
            )
            this
        }
    }
}
