package com.pubnub.framework.util

import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.Typing
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.flow.single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class TypingIndicator(private val pubNub: PubNub, private val userId: UserId) {
    companion object {
        private const val TYPING_ON = "typing_on"
        private const val TYPING_OFF = "typing_off"
    }

    /**
     * Set typing state for channel ID
     */
    suspend fun setTyping(
        channelId: ChannelId,
        isTyping: Boolean,
        onComplete: (PNPublishResult) -> Unit = {},
        onError: (Exception) -> Unit,
    ) {
        pubNub
            .signal(
                channel = channelId,
                message = if (isTyping) TYPING_ON else TYPING_OFF
            )
            .single(
                onError = { onError(it) },
                onComplete = { onComplete(it) }
            )
    }

    /**
     * Listen for typing everywhere or only on provided channel
     * @param channelId to listen on or null to listen on every subscribed channel
     */
    fun getTyping(channelId: ChannelId? = null): Flow<Typing> =
        getEventSignal()
            .filter { it.isTypingEvent && (channelId == null || channelId == it.channel) }
            .filter { it.publisher != userId }
            .map {
                Typing(
                    userId = it.publisher!!,
                    channelId = it.channel,
                    isTyping = it.isTyping,
                    timestamp = it.timetoken ?: System.currentTimeMillis().timetoken
                )
            }

    private fun getEventSignal(): Flow<PNSignalResult> =
        callbackFlow {
            val callback: SubscribeCallback = object : SubscribeCallback() {
                override fun status(pubnub: PubNub, pnStatus: PNStatus) {}

                override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {
                    trySendBlocking(pnSignalResult)
                }
            }
            pubNub.addListener(callback)

            awaitClose { pubNub.removeListener(callback) }
        }

    private val PNSignalResult.isTypingEvent: Boolean
        get() =
            try {
                message.asString in arrayOf(TYPING_ON, TYPING_OFF)
            } catch (e: Exception) {
                false
            }

    private val PNSignalResult.isTyping: Boolean
        get() =
            try {
                message.asString == TYPING_ON
            } catch (e: Exception) {
                false
            }
}
