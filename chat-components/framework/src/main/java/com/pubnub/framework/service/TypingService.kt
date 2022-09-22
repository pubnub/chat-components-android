package com.pubnub.framework.service

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Typing
import com.pubnub.framework.data.TypingMap
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.error.Logger
import com.pubnub.framework.util.Framework
import com.pubnub.framework.util.Seconds
import com.pubnub.framework.util.TypingIndicator
import com.pubnub.framework.util.flow.tickerFlow
import com.pubnub.framework.util.timetoken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

/**
 * Service to send and collect data about typing users
 */
@OptIn(DelicateCoroutinesApi::class)
@Framework
class TypingService constructor(
    private val id: UserId,
    private val typingIndicator: TypingIndicator,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    companion object {
        private const val TIMEOUT: Seconds = 5_000L
        private const val SEND_TIMEOUT: Seconds = 3_000L
    }

    init {
        logger.i("Typing Service $this for User '$id' created")
    }

    private val _typing: MutableSharedFlow<TypingMap> =
        MutableSharedFlow(replay = 1)
    private val typing: Flow<List<Typing>>
        get() = _typing.asSharedFlow()
            .map { it.values.toList() }
            .debounce(1_000L)
            .distinctUntilChanged()


    private var signalJob: Job? = null
    private var typingTimeoutJob: Job? = null

    // region Typing
    /**
     * Listen for typing on provided channel
     */
    fun getTyping(id: ChannelId, filterOwn: Boolean = true): Flow<List<Typing>> =
        typing.map { list ->
            list.filter { data ->
                data.channelId == id // is current channel
                        && (!filterOwn || data.userId != this.id) // user is not own
            }
        }

    /**
     * Set typing state for passed user and channel ID
     */
    suspend fun setTyping(
        userId: UserId,
        channelId: ChannelId,
        isTyping: Boolean,
        timestamp: Long = System.currentTimeMillis().timetoken,
    ) {
        logger.d("Set typing for user '$userId' on '$channelId', value: '$isTyping' at $timestamp")
        val data = Typing(userId, channelId, isTyping, timestamp)
        if (shouldSendTypingEvent(data)) {
            setTypingData(data)
            typingIndicator.setTyping(
                channelId = channelId,
                isTyping = isTyping,
                onError = { logger.e(it, "Cannot send typing signal") }
            )
        }
    }
    // endregion

    // region Binding
    /**
     * Bind for signals and launch timeout timer
     */
    fun bind(id: ChannelId) {
        logger.i("Start listening for typing signal on channel '$id'")
        listenForSignal()
        startTimeoutTimer()
    }

    /**
     * Unbind signal changes listener and stop timeout timer
     */
    fun unbind() {
        logger.i("Stop listening for typing signal")
        stopListenForPresence()
        stopTimeoutTimer()
    }

    /**
     * Listen for incoming signal and process it
     */
    private fun listenForSignal() {
        if (signalJob != null) return
        coroutineScope.launch(dispatcher) {
            signalJob = typingIndicator.getTyping()
                .onEach { setTypingData(it) }
                .launchIn(this)
        }
    }

    /**
     * Cancel incoming signal listener
     */
    private fun stopListenForPresence() {
        signalJob?.cancel()
        signalJob = null
    }
    // endregion

    // region Timer
    /**
     * Start timer which will check for outdated items every 1s
     * @see [removeOutdated]
     */
    private fun startTimeoutTimer() {
        if (typingTimeoutJob != null) return
        coroutineScope.launch(Dispatchers.IO) {
            typingTimeoutJob = tickerFlow(1.seconds)
                .onEach { removeOutdated() }
                .launchIn(this)
        }
    }

    /**
     * Cancel running timer and typing timeout flow
     */
    private fun stopTimeoutTimer() {
        typingTimeoutJob?.cancel()
        typingTimeoutJob = null
    }
    // endregion

    // region Typing event
    private fun getTypingMap(): TypingMap = _typing.replayCache.lastOrNull() ?: TypingMap()

    private suspend fun emitNewData(map: TypingMap) = _typing.emit(map)

    /**
     * Check if new event should be send
     */
    private fun shouldSendTypingEvent(state: Typing): Boolean {
        val typingMap: TypingMap = getTypingMap()
        return (!typingMap.containsKey(state.userId) // check is no event with this userId
                || typingMap[state.userId]!!.isTyping != state.isTyping // state has changed
                || typingMap[state.userId]!!.shouldResend  // timeout occurred
                )
    }

    /**
     * Remove outdated typing events
     * @see Typing.isOutdated
     */
    private suspend fun removeOutdated() {
        val typingMap = getTypingMap()
        val outdated = typingMap.filterValues { it.isOutdated }
        outdated.values.toList().forEach {
            setTypingData(it.userId, it.channelId, false)
        }
    }

    /**
     * Set new typing state for passed user and channel
     * Will produce new channel item
     */
    private suspend fun setTypingData(typingData: Typing) {
        val newList = getTypingMap()

        // remove previous data first
        newList.remove(typingData.userId)

        // set new data
        if (typingData.isTyping)
            newList[typingData.userId] = typingData

        emitNewData(newList)
    }

    private suspend fun setTypingData(
        userId: UserId,
        channelId: ChannelId,
        isTyping: Boolean,
        timestamp: Long = System.currentTimeMillis().timetoken,
    ) =
        setTypingData(Typing(userId, channelId, isTyping, timestamp))

    // endregion

    private val Typing.isOutdated: Boolean
        get() = timestamp + TIMEOUT.timetoken <= System.currentTimeMillis().timetoken

    private val Typing.shouldResend: Boolean
        get() = timestamp + SEND_TIMEOUT.timetoken <= System.currentTimeMillis().timetoken
}

val LocalTypingService =
    compositionLocalOf<TypingService> { throw TypingServiceNotInitializedException() }

class TypingServiceNotInitializedException :
    Exception("Typing Service not initialized")