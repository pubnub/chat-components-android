package com.pubnub.components

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.pubnub.components.data.channel.ChannelCustomDataConverter
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DateConverter
import com.pubnub.components.data.channel.DefaultChannelDao
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DefaultMemberDao
import com.pubnub.components.data.member.MemberDataCustomConverter
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.data.membership.DefaultMembershipDao
import com.pubnub.components.data.message.CustomContentConverter
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.DefaultMessageDao
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DefaultMessageActionDao

@Database(
    entities = [
        DBMessage::class,
        DBMessageAction::class,
        DBMember::class,
        DBMembership::class,
        DBChannel::class,
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = DefaultDatabase.UnifiedPayloadMigration::class),
    ]
)
@TypeConverters(
    ChannelCustomDataConverter::class,
    DateConverter::class,
    MemberDataCustomConverter::class,
    CustomContentConverter::class,
)
abstract class DefaultDatabase : RoomDatabase(),
    PubNubDatabase<DefaultMessageDao, DefaultMessageActionDao, DefaultChannelDao, DefaultMemberDao, DefaultMembershipDao> {
    abstract override fun messageDao(): DefaultMessageDao
    abstract override fun actionDao(): DefaultMessageActionDao
    abstract override fun channelDao(): DefaultChannelDao
    abstract override fun memberDao(): DefaultMemberDao
    abstract override fun membershipDao(): DefaultMembershipDao

    @RenameColumn(
        tableName = "channel",
        fromColumnName = "avatarURL",
        toColumnName = "profileUrl",
    )
    @RenameColumn(
        tableName = "message",
        fromColumnName = "type",
        toColumnName = "contentType",
    )
    @DeleteColumn(
        tableName = "message",
        columnName = "attachment"
    )
    class UnifiedPayloadMigration : AutoMigrationSpec
}
