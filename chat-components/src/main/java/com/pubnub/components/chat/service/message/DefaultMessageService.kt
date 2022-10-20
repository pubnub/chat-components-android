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
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.repository.message.MessageRepository
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.error.Logger
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.data.PNException
import com.pubnub.framework.util.flow.single
import com.pubnub.framework.util.toJson
import com.pubnub.framework.util.toTimetoken
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(DelicateCoroutinesApi::class)
class DefaultMessageService(
    private val pubNub: PubNub,
    private val userId: UserId,
    private val messageRepository: MessageRepository<DBMessage, DBMessageWithActions>,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val networkMapper: NetworkMessageMapper,
    private val networkHistoryMapper: NetworkMessageHistoryMapper,
    private val messageActionHistoryMapper: NetworkMessageActionHistoryMapper,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope = GlobalScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MessageService<NetworkMessagePayload> {

    private lateinit var messageJob: Job

    private val newPubNub: com.pubnub.api.coroutine.PubNub = com.pubnub.api.coroutine.PubNub(pubNub.configuration)
    private val newSubscribe: com.pubnub.api.coroutine.Subscribe = com.pubnub.api.coroutine.Subscribe(pubNub)

    /**
     * Start listening for messages
     */
    override fun bind(vararg channels: String) {
        logger.d("Start listening for incoming messages")
        listenForMessages(*channels)
    }

    /**
     * Stop listening for messages
     */
    override fun unbind() {
        if (::messageJob.isInitialized) {
            stopListenForMessages()
            logger.d("Stopped listening for incoming messages")
        }
    }

    /**
     * Send a message to all subscribers of a channel.
     *
     * @param channelId ID of channel to send a message to
     * @param message payload to send
     * @param meta additional metadata to send
     * @param store flag to keep the message in history
     * @param onSuccess Action to be fired after a successful sent
     * @param onError Action to be fired after an error
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun send(
        channelId: ChannelId,
        message: NetworkMessagePayload,
        meta: Any?,
        store: Boolean,
        onSuccess: (String, Timetoken) -> Unit,
        onError: (Exception) -> Unit,
    ) {

        // Create DB object with status not sent
        val dbMessage = DBMessage(
            id = message.id,
            text = message.text,
            contentType = message.contentType ?: "default",
            content = message.content,
            custom = message.custom,
            publisher = userId,
            channel = channelId,
            timetoken = message.createdAt.toTimetoken(),
            isSent = false,
            exception = null,
        )

        // Add message to repository
        messageRepository.insertOrUpdate(dbMessage)

        coroutineScope.launch(dispatcher) {

            // Publish a message
            val result = newPubNub
                .publish(
                    channel = channelId,
                    message = message,
                    shouldStore = store,
                    meta = meta,
                )

            if(result.isSuccess) {
                // Set message status in repository
                coroutineScope.launch(dispatcher) {

                    messageRepository.setSent(
                        message.id,
                        result.getOrNull()
                    )

                    coroutineScope.launch(Dispatchers.Main) {
                        onSuccess(message.text, result.getOrNull()!!)
                    }
                }
            } else {
                val exception = result.exceptionOrNull()!!
                        // Set message status in repository
                        coroutineScope.launch(dispatcher) {
                            messageRepository.setSendingError(
                                message.id,
                                exception.message
                            )
                            coroutineScope.launch(Dispatchers.Main) {
                                if (exception is PNException)
                                    onError(exception)
                            }
                            logger.e(exception)
                        }
                    }

        }
    }

    /**
     * Get historical messages between passed start - end dates and store it in database
     *
     * @param id ID of channel to return history messages from
     * @param start of time window, newest date (in microseconds)
     * @param end of time window, last known message timestamp + 1 (in microseconds)
     * @param count of messages, default and maximum is 100
     * @param withActions
     * @param withUUID
     */
    override suspend fun fetchAll(
        id: ChannelId,
        start: Long?,
        end: Long?,
        count: Int,
        withActions: Boolean,
        withUUID: Boolean,
    ) {

            val result = newPubNub.fetchMessages(
                channels = listOf(id),
                page = PNBoundedPage(start = start, end = end, limit = count),
                includeMessageActions = withActions,
                includeUUID = withUUID,
            )

            if(result.isSuccess) {
                    // Store messages
                    result.getOrNull()!!.channels.forEach { (channel, messages) ->

                        // Just in case of message mapper issue
                        messages.sortedByDescending { it.timetoken }.onEach {
                            try {
                                val message = networkHistoryMapper.map(channel, it)
                                insertOrUpdate(message)
                            } catch (e: Exception) {
                                logger.e(
                                    e,
                                    "Cannot map message ${it.message.toJson(pubNub.mapper)}"
                                )
                            }
                            try {
                                val actions = messageActionHistoryMapper.map(id, it)
                                insertMessageAction(*actions)

                            } catch (e: Exception) {
                                logger.e(
                                    e,
                                    "Cannot map message action ${it.toJson(pubNub.mapper)}"
                                )
                            }
                        }
                    }
                }
        else {
                logger.e(result.exceptionOrNull(), "Cannot pull history")
            }
    }

    private fun insertMessageAction(vararg action: DBMessageAction) {
        coroutineScope.launch(dispatcher) {
            messageActionRepository.insertOrUpdate(*action)
        }
    }

    // region Inner binding
    private fun listenForMessages(vararg channels: String) {
        coroutineScope.launch(dispatcher) {
            messageJob = newSubscribe.messageFlow(channels.toList())
                .onEach { it.processMessage() }
                .launchIn(this)
        }
    }

    private fun stopListenForMessages() {
        messageJob.cancel()
    }
    // endregion

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
                logger.e(e, "Cannot map message")
            }
        }
    }

    /**
     * Validates an incoming message, decrypt it and store in database
     */
    private fun handleIncomingMessage(result: NetworkMessage) {
        with(result) {
            if (publisher == null) {
                logger.e(RuntimeException("User cannot be null"))
                return
            }
            if (timetoken == null) {
                logger.e(RuntimeException("Timestamp cannot be null"))
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