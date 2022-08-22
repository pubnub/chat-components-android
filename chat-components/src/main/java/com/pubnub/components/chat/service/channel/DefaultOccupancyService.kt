package com.pubnub.components.chat.service.channel

import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Occupancy
import com.pubnub.framework.data.OccupancyMap
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.OccupancyMapper
import com.pubnub.framework.service.error.Logger
import com.pubnub.framework.util.flow.coroutine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

/**
 * Occupancy Service responsible for notifying about channels occupation changes
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    DelicateCoroutinesApi::class,
    FlowPreview::class
)
class DefaultOccupancyService(
    private val pubNub: PubNub,
    private val userId: UserId,
    private val occupancyMapper: OccupancyMapper,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OccupancyService {

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
            val currentUserId = userId
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
     * @param channel for occupancy check
     */
    override fun getOccupancy(channel: ChannelId): Flow<Occupancy> =
        occupancy.map { it[channel] ?: Occupancy(channel, 0, emptyList()) }

    override suspend fun getPresence(presence: Presence): Presence {

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
                                && member != userId // is not current user
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

    override fun isOnline(user: UserId): Flow<Boolean> =
        online.map { it[user] ?: false }

    // region Binding
    override fun bind() {
        logger.d("Start listening for presence")
        listenForPresence()
        // Get lobby occupancy
        coroutineScope.launch(dispatcher) {
            callHereNow()
        }
    }

    override fun unbind() {
        // unsubscribe
        stopListenForPresence()
        logger.d("Stop listening for presence")

    }
    // endregion

    private suspend fun callHereNow() {
        try {
            getOccupancy()?.let { setOccupancy(it) }
        } catch (e: Exception) {
            logger.w(e, "Cannot set occupancy")
        }
    }

    /**
     * Listen for incoming presence and process it
     */
    private fun listenForPresence() {
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
        logger.i("Set occupancy '$occupancy'")
        _occupancy.emit(occupancy)
    }

    private suspend fun processAction(action: PNPresenceEventResult) {
        logger.d("Process action: '$action'")
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
