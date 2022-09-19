package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.chat.ui.component.presence.Presence
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
    compositionLocalOf<OccupancyService> { throw OccupancyServiceNotInitializedException() }

class OccupancyServiceNotInitializedException :
    Exception("Occupancy Service not initialized")