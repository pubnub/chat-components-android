package com.pubnub.components.chat.viewmodel.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pubnub.components.chat.service.message.action.DefaultMessageReactionService
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.error.Logger

class ReactionViewModelFactory constructor(
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val messageReactionService: DefaultMessageReactionService? = null,
    private val logger: Logger,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReactionViewModel::class.java)) {
            return ReactionViewModel(
                messageActionRepository,
                messageReactionService,
                logger,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
