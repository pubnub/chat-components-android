package com.pubnub.components.chat.ui.component.input

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pubnub.components.chat.provider.LocalLogger
import com.pubnub.components.chat.service.message.LocalMessageService
import com.pubnub.components.chat.service.message.MessageService
import com.pubnub.components.chat.ui.component.provider.LocalUser
import com.pubnub.components.data.message.DBMessage
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.LocalTypingService
import com.pubnub.framework.service.TypingService
import com.pubnub.framework.service.error.Logger
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.timetoken
import com.pubnub.framework.util.toIsoString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * [MessageInputViewModel] component is responsible for sending messages, and showing typing
 * indicator. It allows you to define actions invoked after message sending confirmation, or after
 * error received.
 */
class MessageInputViewModel(
    private val id: UserId,
    private val messageService: MessageService<DBMessage>,
    private val typingService: TypingService? = null,
    private val logger: Logger,
    ) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of [MessageInputViewModel]. To use
         * predefined [TypingService], @see [defaultWithTypingService()]
         *
         * @param id ID of current user
         * @param messageService Message Service implementation
         * @param logger Logger implementation
         * @param typingService Typing Service implementation
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            id: UserId = LocalUser.current,
            messageService: MessageService<DBMessage> = LocalMessageService.current,
            logger: Logger = LocalLogger.current,
            typingService: TypingService? = null,
        ): MessageInputViewModel =
            viewModel(
                factory = MessageInputViewModelFactory(id, messageService, logger, typingService)
            )

        /**
         * Returns default implementation of [MessageInputViewModel] with predefined [TypingService]
         *
         * @param id ID of current user
         * @param messageService Message Service implementation
         *
         * @return ViewModel instance
         */
        @Composable
        fun defaultWithTypingService(
            id: UserId = LocalUser.current,
            messageService: MessageService<DBMessage> = LocalMessageService.current,
        ): MessageInputViewModel =
            default(id, messageService, LocalLogger.current, LocalTypingService.current)
    }

    private val time: Timetoken get() = System.currentTimeMillis().timetoken

    /**
     * Send a message to all subscribers of a channel, and store it in local database.
     *
     * @param id ID of the channel
     * @param message Text to send
     * @param contentType Type of a message content
     * @param content Map of extra data
     * @param onSuccess Action to be fired after a successful sent
     * @param onError Action to be fired after an error
     */
    @Suppress("NAME_SHADOWING")
    fun send(
        id: ChannelId,
        message: String,
        contentType: String? = null,
        content: Map<String, Any?>? = null,
        onSuccess: (String, Timetoken) -> Unit = { _: String, _: Timetoken -> },
        onError: (Exception) -> Unit = { _: Exception -> }
    ) {
        logger.i("Sending message '%s' to channel '%s'", message, id)
        val data = create(id, message, contentType, content)
        viewModelScope.launch(Dispatchers.IO) {
            messageService.send(
                id = id,
                message = data,
                meta = hashMapOf("uuid" to this@MessageInputViewModel.id),
                onSuccess = { message: String, timetoken: Timetoken ->
                    logger.d("Message sent successfully at %s", timetoken.toIsoString())
                    onSuccess(message, timetoken)
                },
                onError = { exception: Exception ->
                    logger.e(exception, "Message cannot be sent")
                    onError(exception)
                },
            )
        }
    }

    private fun create(
        id: ChannelId,
        text: String,
        contentType: String? = null,
        content: Map<String, Any?>? = null,
    ) =
        DBMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            contentType = contentType,
            content = content,
            createdAt = time.toIsoString(),
            publisher = this.id,
            channel = id,
            custom = null,
            timetoken = time,
            isSent = false,
        )

    /**
     * Sets the current user typing state on passed channel
     *
     * @param id ID of the channel
     * @param isTyping [Boolean.true] if user is typing, [Boolean.false] otherwise
     */
    fun setTyping(id: ChannelId, isTyping: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            typingService?.setTyping(this@MessageInputViewModel.id, id, isTyping)
        }
    }
}

class MessageInputViewModelFactory(
    private val id: UserId,
    private val messageService: MessageService<DBMessage>,
    private val logger: Logger,
    private val typingService: TypingService? = null,
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageInputViewModel(id, messageService, typingService, logger) as T
    }
}

