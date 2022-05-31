package com.pubnub.components.chat.network.data

import com.pubnub.api.models.consumer.objects.channel.PNChannelMetadata

typealias NetworkChannelMetadata = PNChannelMetadata

// TODO: remove it when type and status will be returned in response
val NetworkChannelMetadata.type: String? get() = null
val NetworkChannelMetadata.status: String? get() = null
