package com.pubnub.components

import com.pubnub.components.data.channel.ChannelDao
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.data.member.MemberDao
import com.pubnub.components.data.membership.*
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.MessageDao
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.data.message.action.MessageActionDao

interface PubNubDatabase<Message : MessageDao<*, *>, Action : MessageActionDao<*>, Channel : ChannelDao<*, *>, Member : MemberDao<*, *>, Membership : MembershipDao<*>> {
    fun messageDao(): Message
    fun actionDao(): Action
    fun channelDao(): Channel
    fun memberDao(): Member
    fun membershipDao(): Membership
}

@Suppress("UNCHECKED_CAST")
fun DefaultDatabase.asPubNub() =
    this as PubNubDatabase<MessageDao<DBMessage, DBMessageWithActions>, MessageActionDao<DBMessageAction>, ChannelDao<DBChannel, DBChannelWithMembers>, MemberDao<DBMember, DBMemberWithChannels>, MembershipDao<DBMembership>>
