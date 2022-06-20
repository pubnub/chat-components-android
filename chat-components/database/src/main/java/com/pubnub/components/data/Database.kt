package com.pubnub.components.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.pubnub.components.DefaultDatabase
import com.pubnub.components.PubNubDatabase
import com.pubnub.components.asPubNub
import com.pubnub.components.data.channel.ChannelDao
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.data.member.MemberDao
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.data.membership.MembershipDao
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.MessageDao
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.data.message.action.MessageActionDao

object Database {
    private const val DATABASE_NAME = "pubnub_database"

    fun initialize(
        applicationContext: Context,
        builder: (RoomDatabase.Builder<DefaultDatabase>) -> RoomDatabase.Builder<DefaultDatabase> = { it }
    ): DefaultDatabase =
        Room.databaseBuilder(applicationContext, DefaultDatabase::class.java, DATABASE_NAME)
            .addMigrations(MESSAGE_REACTION_MIGRATION)
            .apply { builder(this) }
            .build()

    // region Migrations
    private val MESSAGE_REACTION_MIGRATION = Migration(1, 2) {
        it.execSQL("CREATE TABLE IF NOT EXISTS `message_action` (`channel` TEXT NOT NULL, `user` TEXT NOT NULL, `messageTimestamp` INTEGER NOT NULL, `published` INTEGER NOT NULL, `type` TEXT NOT NULL, `value` TEXT NOT NULL, `id` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }
    // endregion
}