package com.pubnub.components

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pubnub.components.data.channel.ChannelCustomDataConverter
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DateConverter
import com.pubnub.components.data.channel.DefaultChannelDao
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DefaultMemberDao
import com.pubnub.components.data.member.MemberDataCustomConverter
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.data.membership.DefaultMembershipDao
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.DefaultMessageDao
import com.pubnub.components.data.message.MessageAttachmentConverter
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
    version = 2,
    exportSchema = false,
)
@TypeConverters(
    MessageAttachmentConverter::class,
    ChannelCustomDataConverter::class,
    DateConverter::class,
    MemberDataCustomConverter::class,
)
abstract class DefaultDatabase : RoomDatabase(),
    PubNubDatabase<DefaultMessageDao, DefaultMessageActionDao, DefaultChannelDao, DefaultMemberDao, DefaultMembershipDao> {
    abstract override fun messageDao(): DefaultMessageDao
    abstract override fun reactionDao(): DefaultMessageActionDao
    abstract override fun channelDao(): DefaultChannelDao
    abstract override fun memberDao(): DefaultMemberDao
    abstract override fun membershipDao(): DefaultMembershipDao
}
