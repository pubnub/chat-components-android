package com.pubnub.components.chat.service.channel

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.framework.data.Occupancy
import com.pubnub.framework.data.OccupancyMap
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.OccupancyMapper
import com.pubnub.framework.util.flow.coroutine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber

/**
 * Occupancy Service responsible for notifying about channels occupation changes
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    DelicateCoroutinesApi::class,
    FlowPreview::class
)
class OccupancyService(
    private val pubNub: PubNub,
    private val occupancyMapper: OccupancyMapper,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val _occupancy: MutableSharedFlow<OccupancyMap> = MutableSharedFlow(replay = 1)
    val occupancy: Flow<OccupancyMap>
        get() = _occupancy.asSharedFlow()

    val online: Flow<HashMap<UserId, Boolean>> = occupancy.map {
        HashMap(
            it.values
                .filter { it.list != null }
                .flatMap { it.list!!.map { it } }
                .associateBy({ it }, { true })
        ).apply {
            // workaround - add current member as online
            val currentUserId = pubNub.configuration.uuid
            this[currentUserId] = true
        }
    }

    private val _listener = object : SubscribeCallback() {
        override fun status(pubnub: PubNub, pnStatus: PNStatus) {
            // empty
        }

        override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
            super.presence(pubnub, pnPresenceEventResult)

            coroutineScope.launch(dispatcher) { processAction(pnPresenceEventResult) }
        }
    }

    /**
     * Returns occupancy for passed channel
     * @param channelId for occupancy check
     */
    fun getOccupancy(channelId: String): Flow<Occupancy> =
        occupancy.map { it[channelId] ?: Occupancy(channelId, 0, emptyList()) }

    suspend fun getPresence(presence: Presence = Presence()): Presence {
        val currentUserId = pubNub.configuration.uuid

        coroutineScope {
            // Add new occupants
            launch(Dispatchers.Main) {
                online.collect { map ->
                    map.forEach { (member, state) ->
                        presence.set(member, state)
                    }

                    // Remove old occupants
                    presence.filter { member, state ->
                        state // was online
                                && member != currentUserId // is not current user
                                && (!map.containsKey(member) || !map[member]!!) // is not in latest occupancy map
                    }
                        .map { (member, _) ->
                            member
                        }.forEach { member ->
                            presence.set(member, false)
                        }
                }
            }
        }
        return presence
    }

    // region Binding
    fun bind() {
        Timber.e("Bind")
        listenForPresence()
        // Get lobby occupancy
        coroutineScope.launch(dispatcher) {
            callHereNow()
        }
    }

    fun unbind() {
        // unsubscribe
        stopListenForPresence()
    }
    // endregion

    private suspend fun callHereNow() {
        try {
            getOccupancy()?.let { setOccupancy(it) }
        } catch (e: Exception) {
            Timber.w(e, "Cannot set occupancy")
        }
    }

    /**
     * Listen for incoming presence and process it
     */
    private fun listenForPresence() {
        Timber.d("Listen for presence")
        pubNub.addListener(_listener)
    }

    /**
     * Cancel incoming presence listener
     */
    private fun stopListenForPresence() {
        pubNub.removeListener(_listener)
    }

    /**
     * Global Here Now
     */
    private suspend fun getOccupancy(): OccupancyMap? =
        pubNub.hereNow(
            includeUUIDs = true
        )
            .coroutine()
            .let { result ->
                occupancyMapper.map(result.channels)
            }

    private suspend fun setOccupancy(occupancy: OccupancyMap) {
        Timber.i("Set occupancy: $occupancy")
        _occupancy.emit(occupancy)
    }

    private suspend fun processAction(action: PNPresenceEventResult) {
        Timber.d("Process action $action")
        val occupancyMap = _occupancy.replayCache.lastOrNull() ?: OccupancyMap()
        val previousOccupants = occupancyMap[action.channel]
        val occupants = action.getOccupants(previousOccupants)
        val newOccupancy = Occupancy.from(action, occupants) ?: return

        val map = occupancyMap.apply {
            put(action.channel!!, newOccupancy)
        }
        setOccupancy(map)
    }

    private fun PNPresenceEventResult.getOccupants(occupancy: Occupancy?): List<String> =
        (occupancy?.list ?: listOf()).toMutableList().apply {
            when (event) {
                "join" -> add(uuid!!)
                "interval" -> {
                    leave?.let { removeAll { uuid -> uuid in it } }
                    timeout?.let { removeAll { uuid -> uuid in it } }
                    join?.let { addAll(it) }
                }
                "leave", "timeout" -> remove(uuid!!)
            }
        }
}

val LocalOccupancyService =
    staticCompositionLocalOf<OccupancyService> { throw OccupancyServiceNotInitializedException() }

class OccupancyServiceNotInitializedException :
    Exception("Occupancy Service not initialized")
