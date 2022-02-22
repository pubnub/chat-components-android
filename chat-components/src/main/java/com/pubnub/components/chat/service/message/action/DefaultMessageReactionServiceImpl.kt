package com.pubnub.components.chat.service.message.action

import com.pubnub.api.models.consumer.message_actions.PNMessageAction
import com.pubnub.api.models.consumer.pubsub.BasePubSubResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.components.chat.service.error.ErrorHandler
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.service.ActionService
import com.pubnub.framework.util.Timetoken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

@OptIn(
    ExperimentalCoroutinesApi::class,
    FlowPreview::class,
    DelicateCoroutinesApi::class,
)
class DefaultMessageReactionServiceImpl(
    private val userId: UserId,
    private val actionService: ActionService,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val mapper: Mapper<PNMessageActionResult, DBMessageAction>,
    private val errorHandler: ErrorHandler,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MessageReactionService<DBMessageAction> {

    private var actionJob: Job? = null
    private lateinit var types: Array<String>

    /**
     * Start listening for Actions
     *
     * @param types Accepted types of message actions, which will be stored in database
     */
    override fun bind(types: Array<String>) {
        Timber.e("Bind")
        this.types = types
        listenForActions()
    }

    /**
     * Stop listening for Actions
     */
    override fun unbind() {
        Timber.e("Unbind")
        stopListenForActions()
    }

    /**
     * Synchronize message actions for provided channel
     *
     * @param channel ID of channel to synchronize
     * @param lastTimetoken Last synchronization timestamp
     */
    override fun synchronize(channel: ChannelId, lastTimetoken: Long?) {
        Timber.e("Sync actions for channel '$channel'")
        coroutineScope.launch(dispatcher) {
            val lastActionTimestamp =
                lastTimetoken ?: messageActionRepository.getLastTimetoken(channel)
            val actions = actionService.getAll(
                channelId = channel,
                end = lastActionTimestamp + 1,
                start = null,
                limit = 100,
            )
                .filter { it.type in types }
                .map { mapper.map(it.toResult(channel)) }

            insert(*actions.toTypedArray())
        }
    }

    /**
     * Removes existing message action
     *
     * The message action will be removed from the PubNub and local repository
     *
     * @param channel ID of the channel
     * @param messageTimetoken Timetoken of the message
     * @param published Timetoken of the message action
     * @param type Action type
     * @param value Action value
     */
    override suspend fun remove(
        channel: ChannelId,
        messageTimetoken: Timetoken,
        published: Timetoken,
        type: String,
        value: String,
    ) {
        try {
            actionService.remove(channel, messageTimetoken, published)
            removeAction(userId, channel, messageTimetoken, type, value)
        } catch (e: Exception) {
            errorHandler.onError(e, "Cannot remove message action")
        }
    }

    /**
     * Adds the message action
     *
     * The message action will be added to PubNub and local repository
     *
     * @param channel ID of the channel
     * @param messageTimetoken Timetoken of the message
     * @param type Action type
     * @param value Action value
     */
    override suspend fun add(
        channel: ChannelId,
        messageTimetoken: Long,
        type: String,
        value: String,
    ) {
        try {
            val result = actionService.add(channel, PNMessageAction(type, value, messageTimetoken))
                .toResult(channel)
            addAction(result)
        } catch (e: Exception) {
            errorHandler.onError(e, "Cannot add message action")
        }
    }

    /**
     * Stores the message action in the repository
     *
     * @param action Array of [DBMessageAction] objects to store
     */
    private suspend fun insert(vararg action: DBMessageAction) {
        messageActionRepository.insertOrUpdate(*action)
    }

    /**
     * Listen for incoming Actions and process it
     *
     * @see addAction
     * @see removeAction
     */
    private fun listenForActions() {
        coroutineScope.launch(dispatcher) {
            actionJob = actionService.actions
                .filter { it.publisher != userId }
                .onEach { result ->
                    when (result.event) {
                        ActionService.EVENT_ADDED -> addAction(result)
                        ActionService.EVENT_REMOVED -> removeAction(result)
                    }
                }
                .launchIn(this)
        }
    }

    /**
     * Cancel incoming Actions listener
     */
    private fun stopListenForActions() {
        actionJob?.cancel()
        actionJob = null
    }

    /**
     * Add message action to local repository
     *
     * @param result PubNub result object
     */
    private suspend fun addAction(result: PNMessageActionResult) {
        messageActionRepository.insertOrUpdate(mapper.map(result))
    }

    /**
     * Remove message action from local repository
     *
     * @param result PubNub result object
     */
    private suspend fun removeAction(result: PNMessageActionResult) {
        with(result) {
            removeAction(
                user = data.uuid!!,
                channel = channel,
                messageTimetoken = data.messageTimetoken,
                type = data.type,
                value = data.value
            )
        }
    }

    /**
     * Remove message action from repository
     *
     * @param user ID of the user
     * @param channel ID of the channel
     * @param messageTimetoken Timetoken of the message
     * @param type Action type
     * @param value Action value
     */
    private suspend fun removeAction(
        user: UserId,
        channel: ChannelId,
        messageTimetoken: Timetoken,
        type: String,
        value: String
    ) {
        val action = messageActionRepository.get(user, channel, messageTimetoken, type, value)
        Timber.e("Remove action $action")
        if (action != null)
            messageActionRepository.remove(action)
    }
}

// Just a workaround to use one external mapper for both PNMessageAction and PNMessageActionResult
private fun PNMessageAction.toResult(channel: ChannelId): PNMessageActionResult =
    PNMessageActionResult(
        result = BasePubSubResult(channel, null, null, null, uuid),
        event = ActionService.EVENT_ADDED,
        data = this,
    )
