package com.pubnub.components.chat.network.mapper

import com.pubnub.api.managers.MapperManager
import com.pubnub.components.chat.network.data.NetworkMember
import com.pubnub.components.data.member.DBMember
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.asObject

class NetworkMemberMapper(private val mapper: MapperManager) : Mapper<NetworkMember, DBMember> {
    override fun map(input: NetworkMember): DBMember =
        DBMember(
            id = input.id,
            name = input.name ?: input.id,
            email = input.email,
            externalId = input.externalId,
            profileUrl = input.profileUrl,
            type = input.type ?: "default",
            status = input.status,
            custom = input.custom.asObject(mapper),
            eTag = input.eTag,
            updated = input.updated,
        )
}
