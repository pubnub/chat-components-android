package com.pubnub.components.chat.ui.mapper.member

import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.data.message.asMap
import com.pubnub.framework.mapper.Mapper

class DBMemberMapper : Mapper<DBMemberWithChannels, MemberUi.Data> {
    override fun map(input: DBMemberWithChannels): MemberUi.Data =
        MemberUi.Data(input.id,
            input.name,
            input.profileUrl,
            input.member.custom.asMap()?.get("description") as? String?)
}
