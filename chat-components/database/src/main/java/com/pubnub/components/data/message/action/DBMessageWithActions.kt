package com.pubnub.components.data.message.action

import androidx.room.Embedded
import androidx.room.Relation
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.Message

data class DBMessageWithActions(
    @Embedded
    val message: DBMessage,

    @Relation(
        parentColumn = "timetoken",
        entityColumn = "messageTimestamp"
    )
    val actions: List<DBMessageAction>,
) : Message by message