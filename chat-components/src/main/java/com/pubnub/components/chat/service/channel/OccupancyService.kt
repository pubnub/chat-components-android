package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.api.models.consumer.objects.PNPage
import com.pubnub.api.models.consumer.objects.PNSortKey
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.data.channel.Channel
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Occupancy
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

interface OccupancyService {
    fun bind()
    fun unbind()
    fun getOccupancy(channel: ChannelId): Flow<Occupancy>
    suspend fun getPresence(presence: Presence = Presence()): Presence
    fun isOnline(user: UserId): Flow<Boolean>
}

val LocalOccupancyService =
    staticCompositionLocalOf<OccupancyService> { throw OccupancyServiceNotInitializedException() }

class OccupancyServiceNotInitializedException :
    Exception("Occupancy Service not initialized")