package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.api.models.consumer.objects.PNSortKey
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.data.ChannelId

interface ChannelService<Data : Channel> {
    fun bind(vararg channels: String)
    fun unbind()
    fun getAll(
        limit: Int? = null,
        page: PNPage? = null,
        filter: String? = null,
        sort: Collection<PNSortKey> = listOf(),
        includeCustom: Boolean = true,
    )

    fun remove(id: ChannelId)
}

val LocalChannelService =
    staticCompositionLocalOf<ChannelService<DBChannel>> { throw ChannelServiceNotInitializedException() }

class ChannelServiceNotInitializedException :
    Exception("Channel Service not initialized")