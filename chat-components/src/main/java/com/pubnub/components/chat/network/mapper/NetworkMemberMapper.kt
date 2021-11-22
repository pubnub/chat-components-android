package com.pubnub.components.chat.network.mapper

import com.pubnub.api.managers.MapperManager
import com.pubnub.api.models.consumer.objects.uuid.PNUUIDMetadata
import com.pubnub.components.data.member.DBMember
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.asObject

class NetworkMemberMapper(private val mapper: MapperManager) : Mapper<PNUUIDMetadata, DBMember> {
    override fun map(input: PNUUIDMetadata): DBMember =
        DBMember(
            id = input.id,
            name = input.name!!,
            email = input.email,
            externalId = input.externalId,
            profileUrl = input.profileUrl,
            custom = input.custom.asObject(mapper),
            eTag = input.eTag,
            updated = input.updated,
        )
}
