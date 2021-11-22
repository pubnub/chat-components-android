package com.pubnub.components.data.channel

import androidx.annotation.Keep
import androidx.room.*
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.framework.data.ChannelId

@Keep
@Entity(tableName = "channel")
data class DBChannel(
    @PrimaryKey @ColumnInfo(name = "channelId") override val id: ChannelId,
    override val name: String,
    override val description: String?,
    override val type: String = "default",
    override val updated: String?,
    override val eTag: String?,
    override val avatarURL: String,
    override val custom: ChannelCustomData,
) : Channel

typealias ChannelCustomData = HashMap<String, Any>

data class DBChannelWithMembers(
    @Embedded val channel: DBChannel,
    @Relation(
        parentColumn = "channelId",
        entityColumn = "memberId",
        associateBy = Junction(DBMembership::class)
    )
    val members: List<DBMember>
) : Channel by channel
