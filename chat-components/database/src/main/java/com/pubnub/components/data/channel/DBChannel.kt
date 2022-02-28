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
    override val description: String? = null,
    override val type: String = "default",
    override val updated: String? = null,
    override val eTag: String? = null,
    override val avatarURL: String? = null,
    override val custom: ChannelCustomData? = null,
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
