package com.pubnub.components.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
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
import com.pubnub.components.data.message.action.MessageActionDao

object Database {
    private const val DATABASE_NAME = "pubnub_database"

    lateinit var INSTANCE: PubNubDatabase<MessageDao<DBMessage, DBMessage>, MessageActionDao<DBMessageAction>, ChannelDao<DBChannel, DBChannelWithMembers>, MemberDao<DBMember, DBMemberWithChannels>, MembershipDao<DBMembership>>

    fun initialize(
        applicationContext: Context,
        builder: (RoomDatabase.Builder<DefaultDatabase>) -> RoomDatabase.Builder<DefaultDatabase> = { it }
    ): DefaultDatabase =
        Room.databaseBuilder(applicationContext, DefaultDatabase::class.java, DATABASE_NAME)
            .apply { builder(this) }
            .build()
            .also {
                Log.e("TAG", "Database build")
                INSTANCE = it.asPubNub()
            }
}