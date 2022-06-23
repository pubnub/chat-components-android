package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.api.models.consumer.objects.PNKey
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.api.models.consumer.objects.PNSortKey
import com.pubnub.api.models.consumer.objects.SortField
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.data.ChannelId

interface ChannelService<Data : Channel> {
    fun bind(vararg channels: String)
    fun unbind()
    fun fetch(
        id: ChannelId,
        includeCustom: Boolean = false,
    )

    fun fetchAll(
        limit: Int? = null,
        page: PNPage? = null,
        filter: String? = null,
        sort: Collection<PNSortKey<PNKey>> = listOf(),
        includeCustom: Boolean = true,
    )

    fun add(channel: Data, includeCustom: Boolean)
    fun remove(id: ChannelId)
}

val LocalChannelService =
    staticCompositionLocalOf<ChannelService<DBChannel>> { throw ChannelServiceNotInitializedException() }

class ChannelServiceNotInitializedException :
    Exception("Channel Service not initialized")