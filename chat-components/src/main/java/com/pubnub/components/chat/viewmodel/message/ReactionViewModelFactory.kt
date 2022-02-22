package com.pubnub.components.chat.viewmodel.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.pubnub.components.chat.service.message.action.DefaultMessageReactionServiceImpl
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class ReactionViewModelFactory constructor(
    private val channelId: ChannelId,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val messageReactionService: DefaultMessageReactionServiceImpl? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReactionViewModel::class.java)) {
            return ReactionViewModel(
                channelId,
                messageActionRepository,
                messageReactionService,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
