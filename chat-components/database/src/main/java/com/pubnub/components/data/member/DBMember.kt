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
    override val name: String,
    override val email: String?,
    override val externalId: String?,
    override val profileUrl: String?,
    override val custom: CustomData,
    override val eTag: String?,
    override val updated: String?,
) : Member {
    @Keep
    data class CustomData(
        val description: String
    )
}

data class DBMemberWithChannels(
    @Embedded val member: DBMember,
    @Relation(
        parentColumn = "memberId",
        entityColumn = "channelId",
        associateBy = Junction(DBMembership::class)
    )
    val channels: List<DBChannel>
) : Member by member
