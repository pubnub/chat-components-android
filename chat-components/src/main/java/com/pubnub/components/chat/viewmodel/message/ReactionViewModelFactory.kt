package com.pubnub.components.chat.viewmodel.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.pubnub.components.chat.service.message.action.DefaultMessageReactionService
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.error.ErrorHandler
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class ReactionViewModelFactory constructor(
    private val userId: UserId,
    private val channelId: ChannelId,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val messageReactionService: DefaultMessageReactionService? = null,
    private val errorHandler: ErrorHandler,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReactionViewModel::class.java)) {
            return ReactionViewModel(
                userId,
                channelId,
                messageActionRepository,
                messageReactionService,
                errorHandler,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
