package com.pubnub.components.chat.viewmodel.message

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.*
import com.pubnub.components.chat.network.paging.MessageRemoteMediator
import com.pubnub.components.chat.provider.LocalMemberFormatter
import com.pubnub.components.chat.provider.LocalMessageRepository
import com.pubnub.components.chat.service.channel.LocalOccupancyService
import com.pubnub.components.chat.service.channel.OccupancyService
import com.pubnub.components.chat.service.message.LocalMessageService
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.components.chat.ui.mapper.message.DBMessageMapper
import com.pubnub.components.chat.ui.mapper.message.DomainMessageMapper
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.repository.message.DefaultMessageRepository
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.isSameDate
import com.pubnub.framework.util.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * [MessageViewModel] contains the logic for getting the list of messages from the repository.
 * The returned object is mapped to UI data and contains only the data needed to be displayed.
 */
@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class MessageViewModel constructor(
    private val currentUserId: UserId,
    private val channelId: ChannelId,
    private val messageRepository: DefaultMessageRepository,
    private val remoteMediator: MessageRemoteMediator?,
    private val presenceService: OccupancyService?,
    private val config: PagingConfig = PagingConfig(pageSize = 10, enablePlaceholders = true),
    private val dbMapper: Mapper<DBMessage, MessageUi.Data>,
    private val uiMapper: Mapper<MessageUi.Data, DBMessage>,
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of MessageViewModel
         * This implementation allows to load a data from database only. For loading the historical
         * messages from network, @see [defaultWithMediator()]
         *
         * @param id ID of the Channel
         * @param mediator Remote Mediator implementation, null by default
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            id: ChannelId = LocalChannel.current,
            mediator: MessageRemoteMediator? = null,
        ): MessageViewModel {
            val messageFactory = MessageViewModelFactory(
                currentUserId = LocalPubNub.current.configuration.uuid,
                channelId = id,
                messageRepository = LocalMessageRepository.current,
                remoteMediator = mediator,
                presenceService = LocalOccupancyService.current,
                dbMapper = DBMessageMapper(LocalMemberFormatter.current),
                uiMapper = DomainMessageMapper(),
            )
            return viewModel(factory = messageFactory)
        }

        /**
         * Returns default implementation of MessageViewModel
         * This implementation allows to load a data from both database and network.
         *
         * @param id ID of the Channel
         *
         * @return ViewModel instance
         */
        @Composable
        fun defaultWithMediator(id: ChannelId = LocalChannel.current): MessageViewModel {
            val repository = LocalMessageRepository.current

            // region Message View Model
            val mediator = MessageRemoteMediator(
                channelId = id,
                service = LocalMessageService.current,
                messageRepository = repository,
            )
            return default(id = id, mediator = mediator)
        }
    }

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private fun Timetoken.formatDate(): String =
        dateFormat.format(this.seconds).lowercase(Locale.getDefault())

    /**
     * Get Messages for selected Channel
     *
     * @return Flow of Message UI Paging Data
     */
    fun getAll(): Flow<PagingData<MessageUi>> =
        Pager(
            config = config,
            pagingSourceFactory = {
                messageRepository.getAll(
                    id = channelId,
                    sorted = arrayOf(Sorted(MessageUi.Data::timetoken.name, Sorted.Direction.DESC)),
                )
            },
            remoteMediator = remoteMediator,
        ).flow.map { paging -> paging.map { it.toMessageUi() } }
            .map { pagingData ->
                pagingData.insertSeparators { after: MessageUi?, before: MessageUi? ->
                    if (
                        (before is MessageUi.Data? && after is MessageUi.Data?) &&
                        before == null && after != null ||
                        before is MessageUi.Data && after is MessageUi.Data &&
                        !before.timetoken.isSameDate(after.timetoken)
                    ) {
                        after as MessageUi.Data
                        MessageUi.Separator(after.timetoken.formatDate())
                    } else null
                }
            }
            .cachedIn(viewModelScope)
            .distinctUntilChanged()

    /**
     * Get current users online / offline state
     *
     * @return Presence object when [presenceService] exists, or null
     */
    fun getPresence(): Presence? {
        if (presenceService == null) return null

        val presence = Presence()
        viewModelScope.launch(Dispatchers.IO) { presenceService.getPresence(presence) }
        return presence
    }

    /**
     * Removes all the messages from repository
     */
    fun removeAll() = viewModelScope.launch { messageRepository.removeAll(channelId) }

    private fun MessageUi.toDb(): DBMessage = uiMapper.map(this as MessageUi.Data)
    private fun DBMessage.toUi(): MessageUi.Data = dbMapper.map(this)
    private fun DBMessage.toMessageUi(): MessageUi = this.toUi()
    private fun List<DBMessage>.toUi(): List<MessageUi.Data> = map { it.toUi() }
}