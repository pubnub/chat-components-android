package com.pubnub.components.chat.ui.mapper.channel

import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.data.member.DBMember
import com.pubnub.framework.mapper.Mapper
import kotlinx.datetime.toInstant

class DBChannelMapper : Mapper<DBChannelWithMembers, ChannelUi.Data> {
    override fun map(input: DBChannelWithMembers): ChannelUi.Data =
        ChannelUi.Data(
            id = input.id,
            name = input.name,
            description = input.description,
            type = ChannelUi.Data.typeFromString(input.channel.type),
            profileUrl = input.channel.avatarURL,
            updated = input.updated?.toInstant(),
            members = input.members.toUi()
        )

    private fun List<DBMember>.toUi(): List<MemberUi.Data> =
        map { input ->
            MemberUi.Data(
                input.id,
                input.name,
                input.profileUrl,
                input.custom.description
            )
        }
}
