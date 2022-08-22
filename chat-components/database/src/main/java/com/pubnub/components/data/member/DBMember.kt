package com.pubnub.components.data.member

import androidx.annotation.Keep
import androidx.room.*
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.framework.data.UserId

@Keep
@Entity(tableName = "member")
data class DBMember(
    @PrimaryKey @ColumnInfo(name = "memberId") override val id: UserId,
    override val name: String? = null,
    override val email: String? = null,
    override val externalId: String? = null,
    override val profileUrl: String? = null,
    @ColumnInfo(defaultValue = "default")
    override val type: String = "default",
    override val status: String? = null,
    override val custom: CustomData? = null,
    override val eTag: String? = null,
    override val updated: String? = null,
) : Member {
    @Keep
    data class CustomData(
        val description: String,
    )
}

data class DBMemberWithChannels(
    @Embedded val member: DBMember,
    @Relation(
        parentColumn = "memberId",
        entityColumn = "channelId",
        associateBy = Junction(DBMembership::class)
    )
    val channels: List<DBChannel>,
) : Member by member
