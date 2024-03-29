package com.pubnub.components.chat.ui.mapper.channel

import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.message.asMap
import com.pubnub.framework.mapper.Mapper
import kotlinx.datetime.toInstant

class DBChannelMapper : Mapper<DBChannelWithMembers, ChannelUi.Data> {
    override fun map(input: DBChannelWithMembers): ChannelUi.Data =
        ChannelUi.Data(
            id = input.id,
            name = input.name ?: input.id,
            description = input.description,
            type = ChannelUi.Data.typeFromString(input.channel.type),
            profileUrl = input.channel.profileUrl,
            updated = input.updated?.toInstant(),
            members = input.members.toUi(),
            status = input.status,
            custom = input.custom,
        )

    private fun List<DBMember>.toUi(): List<MemberUi.Data> =
        map { input ->
            MemberUi.Data(
                id = input.id,
                name = input.name,
                type = input.type,
                email = input.email,
                externalId = input.externalId,
                profileUrl = input.profileUrl,
                description = input.custom.asMap()?.get("description") as? String?,
                status = input.status,
                custom = input.custom,
            )
        }
}
