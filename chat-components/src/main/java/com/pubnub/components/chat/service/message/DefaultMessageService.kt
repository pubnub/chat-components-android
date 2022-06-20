package com.pubnub.components.chat.service.message

import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNBoundedPage
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.components.chat.network.data.NetworkMessage
import com.pubnub.components.chat.network.data.NetworkMessagePayload
import com.pubnub.components.chat.network.mapper.NetworkMessageActionHistoryMapper
import com.pubnub.components.chat.network.mapper.NetworkMessageHistoryMapper
import com.pubnub.components.chat.network.mapper.NetworkMessageMapper
import com.pubnub.components.chat.service.error.ErrorHandler
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.repository.message.MessageRepository
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.data.PNException
import com.pubnub.framework.util.flow.single
import com.pubnub.framework.util.toJson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class DefaultMessageService(
    private val pubNub: PubNub,
    private val messageRepository: MessageRepository<DBMessage, DBMessageWithActions>,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val networkMapper: NetworkMessageMapper,
    private val networkHistoryMapper: NetworkMessageHistoryMapper,
    private val messageActionHistoryMapper: NetworkMessageActionHistoryMapper,
    private val errorHandler: ErrorHandler,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MessageService<DBMessage> {

    private lateinit var messageJob: Job

    /**
     * Start listening for messages
     */
    override fun bind() {
        listenForMessages()
    }

    /**
     * Stop listening for messages
     */
    override fun unbind() {
        if (::messageJob.isInitialized)
            stopListenForMessages()
    }

    /**
     * Send a message to all subscribers of a channel.
     *
     * @param id ID of channel to send a message to
     * @param message object to send
     * @param meta additional metadata to send
     * @param store flag to keep the message in history
     */
    override suspend fun send(
        id: ChannelId,
        message: DBMessage,
        meta: Any?,
        store: Boolean,
        onSuccess: (String, Timetoken) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        // Just override status
        val newMessage = message.copy(isSent = false, exception = null)

        // Add message to repository
        messageRepository.insertOrUpdate(newMessage)

        coroutineScope.launch(dispatcher) {

            val networkMessage = NetworkMessagePayload(
                id = newMessage.id,
                text = newMessage.text,
                contentType = newMessage.contentType,
                content = newMessage.content,
                createdAt = newMessage.createdAt,
                custom = newMessage.custom,
            )
            // Publish a message
            pubNub
                .publish(
                    channel = id,
                    message = networkMessage,
                    shouldStore = store,
                    meta = meta,
                )
                .single(
                    onComplete = { result ->
                        // Set message status in repository
                        coroutineScope.launch(dispatcher) {

                            messageRepository.setSent(
                                newMessage.id,
                                result.timetoken
                            )

                            coroutineScope.launch(Dispatchers.Main) {
                                onSuccess(newMessage.text, result.timetoken)
                            }
                        }
                    },
                    onError = { exception ->
                        // Set message status in repository
                        coroutineScope.launch(dispatcher) {
                            messageRepository.setSendingError(
                                newMessage.id,
                                exception.message
                            )
                            coroutineScope.launch(Dispatchers.Main) {
                                if (exception is PNException)
                                    onError(exception)
                            }
                            errorHandler.onError(exception)
                        }
                    },
                )
        }
    }

    /**
     * Get historical messages between passed start - end dates and store it in database
     *
     * @param id ID of channel to return history messages from
     * @param start of time window, newest date (in microseconds)
     * @param end of time window, last known message timestamp + 1 (in microseconds)
     * @param count of messages, default and maximum is 100
     */
    override suspend fun fetchAll(
        id: ChannelId,
        start: Long?,
        end: Long?,
        count: Int,
        withActions: Boolean,
        withUUID: Boolean,
    ) {

        pubNub
            .fetchMessages(
                channels = listOf(id),
                page = PNBoundedPage(start = start, end = end, limit = count),
                includeMessageActions = withActions,
                includeUUID = withUUID,
            )
            .single(
                onComplete = { result ->
                    // Store messages
                    result.channels.forEach { (channel, messages) ->

                        // Just in case of message mapper issue
                        messages.sortedByDescending { it.timetoken }.onEach {
                            try {
                                val message = networkHistoryMapper.map(channel, it)
                                insertOrUpdate(message)
                            } catch (e: Exception) {
                                errorHandler.onError(
                                    e,
                                    "Cannot map message ${it.message.toJson(pubNub.mapper)}"
                                )
                            }
                            try {
                                val actions = messageActionHistoryMapper.map(id, it)
                                insertMessageAction(*actions)

                            } catch (e: Exception) {
                                errorHandler.onError(
                                    e,
                                    "Cannot map message action ${it.toJson(pubNub.mapper)}"
                                )
                            }
                        }
                    }
                },
                onError = { exception ->
                    errorHandler.onError(exception, "Cannot pull history")
                }
            )
    }

    private fun insertMessageAction(vararg action: DBMessageAction) {
        coroutineScope.launch(dispatcher) {
            messageActionRepository.insertOrUpdate(*action)
        }
    }

    // region Inner binding
    private fun listenForMessages() {
        coroutineScope.launch(dispatcher) {
            messageJob = messageFlow()
                .onEach { it.processMessage() }
                .launchIn(this)
        }
    }

    private fun stopListenForMessages() {
        messageJob.cancel()
    }
    // endregion

    private fun messageFlow(): Flow<NetworkMessage> =
        callbackFlow {
            val callback: SubscribeCallback = object : SubscribeCallback() {
                override fun status(pubnub: PubNub, pnStatus: PNStatus) {}

                override fun message(pubnub: PubNub, pnMessageResult: NetworkMessage) {
                    trySendBlocking(pnMessageResult)
                }
            }
            pubNub.addListener(callback)

            awaitClose { pubNub.removeListener(callback) }
        }

    /**
     * Processing [NetworkMessage] and silently catch exceptions
     *
     * @see [handleIncomingMessage]
     */
    private fun NetworkMessage.processMessage() {
        coroutineScope.launch(dispatcher) {
            try {
                handleIncomingMessage(this@processMessage)
            } catch (e: Exception) {
                errorHandler.onError(e, "Cannot map message")
            }
        }
    }

    /**
     * Validates an incoming message, decrypt it and store in database
     */
    private fun handleIncomingMessage(result: NetworkMessage) {
        with(result) {
            if (publisher == null) {
                errorHandler.onError(RuntimeException("User cannot be null"))
                return
            }
            if (timetoken == null) {
                errorHandler.onError(RuntimeException("Timestamp cannot be null"))
                return
            }


            val messageData: DBMessage = networkMapper.map(result)
            insertOrUpdate(messageData)
        }
    }

    /**
     * Store new or update existing message
     */
    private fun insertOrUpdate(message: DBMessage) {
        coroutineScope.launch(dispatcher) {
            messageRepository.insertOrUpdate(message)
        }
    }
}