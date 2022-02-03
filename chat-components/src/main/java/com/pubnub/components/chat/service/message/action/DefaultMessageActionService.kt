package com.pubnub.components.chat.service.message.action

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.api.models.consumer.message_actions.PNMessageAction
import com.pubnub.api.models.consumer.pubsub.BasePubSubResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.components.chat.network.mapper.NetworkMessageActionMapper
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.DefaultMessageActionRepository
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
class DefaultMessageActionService(
    private val userId: UserId,
    private val actionService: ActionService,
    private val messageActionRepository: DefaultMessageActionRepository,
    private val mapper: Mapper<PNMessageActionResult, DBMessageAction>,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): MessageActionService<DBMessageAction> {

    private var actionJob: Job? = null
    private lateinit var types: Array<String>

    /**
     * Start listening for Actions
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

    override fun synchronize(channel: ChannelId, lastTimetoken: Long?) {
        Timber.e("Sync actions for channel '$channel'")
        coroutineScope.launch(dispatcher) {
            val lastActionTimestamp = lastTimetoken ?: messageActionRepository.getLastTimetoken(channel)
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

    private suspend fun insert(vararg action: DBMessageAction) {
        messageActionRepository.insertUpdate(*action)
    }

    override suspend fun remove(
        channel: ChannelId,
        messageTimetoken: Timetoken,
        published: Timetoken,
        type: String,
        value: String,
    ) {
        actionService.remove(channel, messageTimetoken, published)
        removeAction(userId, channel, messageTimetoken, type, value)
    }

    override suspend fun add(
        channel: ChannelId,
        messageTimetoken: Long,
        type: String,
        value: String,
    ) {
        val result = actionService.add(channel, PNMessageAction(type, value, messageTimetoken)).toResult(channel)
        addAction(result)
    }

    /**
     * Listen for incoming Actions and process it
     * @see process
     */
    private fun listenForActions() {
        coroutineScope.launch(dispatcher) {
            actionJob = actionService.actions
                .filter { it.publisher != userId }
                .onEach { result ->
                    when(result.event){
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
     * Add message action
     */
    private suspend fun addAction(result: PNMessageActionResult){
        messageActionRepository.add(mapper.map(result))
    }

    /**
     * Remove message action
     */
    private suspend fun removeAction(result: PNMessageActionResult) {
        with(result){
            removeAction(user = data.uuid!!, channel = channel, messageTimetoken = data.messageTimetoken, type = data.type, value = data.value)
        }
    }

    /**
     * Remove message action
     */
    private suspend fun removeAction(user: UserId, channel: ChannelId, messageTimetoken: Timetoken, type: String, value: String) {
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
