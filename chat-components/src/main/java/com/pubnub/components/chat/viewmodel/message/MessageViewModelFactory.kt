package com.pubnub.components.chat.viewmodel.message

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import com.pubnub.components.chat.network.paging.MessageRemoteMediator
import com.pubnub.components.chat.service.channel.OccupancyService
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.repository.message.MessageRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.Mapper
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class MessageViewModelFactory constructor(
    private val channelId: ChannelId,
    private val messageRepository: MessageRepository<DBMessage, DBMessageWithActions>,
    private val config: PagingConfig = PagingConfig(pageSize = 10, enablePlaceholders = true),
    private val remoteMediator: MessageRemoteMediator? = null,
    private val presenceService: OccupancyService? = null,
    private val clipboardManager: ClipboardManager,
    private val dbMapper: Mapper<DBMessageWithActions, MessageUi.Data>,
    private val uiMapper: Mapper<MessageUi.Data, DBMessageWithActions>,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(
                channelId,
                messageRepository,
                remoteMediator,
                presenceService,
                clipboardManager,
                config,
                dbMapper,
                uiMapper,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
