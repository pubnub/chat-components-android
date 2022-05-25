package com.pubnub.components.chat.network.data

import com.pubnub.api.models.consumer.objects.uuid.PNUUIDMetadata

typealias NetworkMember = PNUUIDMetadata

// TODO: remove it when type and status will be returned in response
val NetworkMember.type: String? get() = null
val NetworkMember.status: String? get() = null
