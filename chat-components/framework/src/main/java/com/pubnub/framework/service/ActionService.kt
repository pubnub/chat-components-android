package com.pubnub.framework.service

import androidx.annotation.StringDef
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNBoundedPage
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.message_actions.PNAddMessageActionResult
import com.pubnub.api.models.consumer.message_actions.PNGetMessageActionsResult
import com.pubnub.api.models.consumer.message_actions.PNMessageAction
import com.pubnub.api.models.consumer.message_actions.PNRemoveMessageActionResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Framework
import com.pubnub.framework.util.flow.single
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import timber.log.Timber

/**
 * Action Service which handle [PNMessageActionResult]
 *
 * Used to send / receive channel invitations
 */
@OptIn(
    ExperimentalCoroutinesApi::class,
    DelicateCoroutinesApi::class,
    FlowPreview::class
)
@Framework
class ActionService(
    private val pubNub: PubNub,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val _actions: MutableSharedFlow<PNMessageActionResult> =
        MutableSharedFlow(replay = 1)
    val actions: Flow<PNMessageActionResult> get() = _actions.asSharedFlow()

    private lateinit var actionJob: Job

    /**
     * Send message action
     *
     * @param channelId you invite to
     * @param messageAction to send
     */
    suspend fun add(
        channelId: ChannelId,
        messageAction: PNMessageAction,
    ): PNAddMessageActionResult {
        Timber.i("Send message action to channel '$channelId': $messageAction")
        return pubNub.addMessageAction(
            channel = channelId,
            messageAction = messageAction,
        ).single()
    }

    /**
     * Remove message action
     *
     * @param channelId Channel to remove message actions from.
     * @param messageTimetoken The publish timetoken of the original message.
     * @param actionTimetoken The publish timetoken of the message action to be removed.
     */
    suspend fun remove(
        channelId: ChannelId,
        messageTimetoken: Long,
        actionTimetoken: Long,
    ): PNRemoveMessageActionResult {
        Timber.i("Remove message action from channel '$channelId', messageTimetoken '$messageTimetoken', actionTimetoken '$actionTimetoken'")
        return pubNub.removeMessageAction(
            channel = channelId,
            messageTimetoken = messageTimetoken,
            actionTimetoken = actionTimetoken
        ).single()
    }

    /**
     * Get message action
     *
     * @param channelId Channel to fetch message actions from.
     * @param start Message Action timetoken denoting the start of the range requested
     *              (return values will be less than start).
     * @param end Message Action timetoken denoting the end of the range requested
     *            (return values will be greater than or equal to end).
     */
    suspend fun get(
        channelId: ChannelId,
        start: Long?,
        end: Long?,
        limit: Int? = null,
    ): PNGetMessageActionsResult {
        Timber.i("Get message action from channel '$channelId', time [$end : $start)")
        return pubNub.getMessageActions(
            channel = channelId,
            page = PNBoundedPage(
                start = start,
                end = end,
                limit = limit
            )
        ).single()
    }


    /**
     * Synchronize message actions
     *
     * @param channelId Channel to fetch message actions from.
     * @param start Message Action timetoken denoting the start of the range requested
     *              (return values will be less than start).
     * @param end Message Action timetoken denoting the end of the range requested
     *            (return values will be greater than or equal to end).
     */
    suspend fun getAll(
        channelId: ChannelId,
        start: Long?,
        end: Long?,
        limit: Int?,
    ): List<PNMessageAction> =
        getAll(channelId, start, end, limit, mutableListOf())

    private suspend fun getAll(
        channelId: ChannelId,
        start: Long?,
        end: Long?,
        limit: Int?,
        results: MutableList<PNMessageAction>,
    ): List<PNMessageAction> {

        val result = get(channelId, start, end, limit)
        val newActions = result.actions
        results.addAll(newActions)

        Timber.e("Sync successful. New actions: ${newActions.size}")
        return when {
            result.page != null -> {
                Timber.i("Page: ${result.page}")
                val newestActionTimestamp = result.page!!.start

                Timber.e("Trying to sync successful with end '$newestActionTimestamp'")
                getAll(channelId, newestActionTimestamp, end, limit, results)
            }
            newActions.isNotEmpty() -> {
                val newestActionTimestamp = newActions.minOf { it.actionTimetoken!! }
                Timber.e("Trying to sync successful with end '$newestActionTimestamp'")
                getAll(channelId, newestActionTimestamp, end, limit, results)
            }
            else -> {
                Timber.e("Sync successful. No more actions. Result size: ${results.size}")
                results
            }
        }
    }

    /**
     * Start listening for Actions
     */
    fun bind() {
        Timber.e("Bind")
        Timber.e("User : ${pubNub.configuration.uuid}")
        listenForActions()
    }

    /**
     * Stop listening for Actions
     */
    fun unbind() {
        Timber.e("Unbind")
        if (::actionJob.isInitialized)
            stopListenForActions()
    }

    /**
     * Listen for incoming Actions and process it
     * @see processAction
     */
    private fun listenForActions() {
        coroutineScope.launch(dispatcher) {
            actionJob = messageActionFlow()
                .onEach { it.processAction() }
                .launchIn(this)
        }
    }

    /**
     * Cancel incoming Actions listener
     */
    private fun stopListenForActions() {
        actionJob.cancel()
    }

    private fun messageActionFlow(): Flow<PNMessageActionResult> =
        callbackFlow {
            val callback: SubscribeCallback = object : SubscribeCallback() {
                override fun status(pubnub: PubNub, pnStatus: PNStatus) {}

                override fun messageAction(
                    pubnub: PubNub,
                    pnMessageActionResult: PNMessageActionResult
                ) {
                    trySendBlocking(pnMessageActionResult)
                }
            }
            pubNub.addListener(callback)

            awaitClose { pubNub.removeListener(callback) }
        }

    /**
     * Process Actions
     *
     * Will check [PNMessageActionResult.messageAction] type and process it.
     */
    private suspend fun PNMessageActionResult.processAction() {
        Timber.i("Action received: $messageAction")
        _actions.emit(this@processAction)
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(EVENT_ADDED, EVENT_REMOVED)
    annotation class Type

    companion object {
        const val EVENT_ADDED = "added"
        const val EVENT_REMOVED = "removed"
    }
}
