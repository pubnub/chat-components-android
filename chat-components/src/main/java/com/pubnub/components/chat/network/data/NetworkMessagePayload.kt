package com.pubnub.components.chat.network.data

import com.pubnub.components.data.message.Message
import java.util.*

data class NetworkMessagePayload(
    override val id: String = UUID.randomUUID().toString(),
    override val text: String,
    override val contentType: String?,
    override val content: Any? = null,
    override val createdAt: String,
    override val custom: Any? = null,
) : Message
