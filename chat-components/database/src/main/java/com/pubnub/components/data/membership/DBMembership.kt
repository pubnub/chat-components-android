package com.pubnub.components.data.membership

import androidx.room.Entity
import androidx.room.Index
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId

@Entity(
    tableName = "membership",
    indices = [Index("channelId"), Index("memberId")],
    primaryKeys = ["channelId", "memberId", "id"]
)
data class DBMembership(
    override val channelId: ChannelId,
    override val memberId: UserId,
    override val id: String = "${channelId}:${memberId}",
) : Membership
