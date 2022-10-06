package com.pubnub.components.chat.viewmodel.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
import com.pubnub.components.chat.ui.mapper.message.DBMessageMapper
import com.pubnub.components.chat.ui.mapper.message.DomainMessageMapper
import com.pubnub.components.data.message.DBMessage
import com.pubnub.components.data.message.action.DBMessageWithActions
import com.pubnub.components.repository.message.MessageRepository
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.mapper.Mapper
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import kotlinx.coroutines.Dispatchers
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
@OptIn(ExperimentalPagingApi::class)
class MessageViewModel constructor(
    private val messageRepository: MessageRepository<DBMessage, DBMessageWithActions>,
    private val remoteMediator: MessageRemoteMediator?,
    private val presenceService: OccupancyService?,
    private val clipboard: ClipboardManager,
    private val config: PagingConfig = PagingConfig(pageSize = 10, enablePlaceholders = true),
    private val dbMapper: Mapper<DBMessageWithActions, MessageUi.Data>,
    private val uiMapper: Mapper<MessageUi.Data, DBMessageWithActions>,
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of MessageViewModel
         * This implementation allows to load a data from database only. For loading the historical
         * messages from network, @see [defaultWithMediator()]
         *
         * @param mediator Remote Mediator implementation, null by default
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            mediator: MessageRemoteMediator? = null,
        ): MessageViewModel {
            val messageFactory = MessageViewModelFactory(
                messageRepository = LocalMessageRepository.current,
                remoteMediator = mediator,
                presenceService = LocalOccupancyService.current,
                clipboardManager = LocalClipboardManager.current,
                dbMapper = DBMessageMapper(LocalMemberFormatter.current),
                uiMapper = DomainMessageMapper(),
            )
            return viewModel(factory = messageFactory)
        }

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
        @Deprecated(
            message = "This method is no longer supported. Please pass ChannelId directly to the corresponding methods.",
            replaceWith = ReplaceWith("default(mediator)"),
            level = DeprecationLevel.ERROR,
        )
        @Composable
        fun default(
            @Suppress("UNUSED_PARAMETER") id: ChannelId = LocalChannel.current,
            mediator: MessageRemoteMediator? = null,
        ): MessageViewModel = default(mediator)

        /**
         * Returns default implementation of MessageViewModel
         * This implementation allows to load a data from both database and network.
         *
         * @return ViewModel instance
         */
        @Composable
        fun defaultWithMediator(): MessageViewModel {
            val repository = LocalMessageRepository.current

            val mediator = MessageRemoteMediator(
                service = LocalMessageService.current,
                messageRepository = repository,
            )
            return default(mediator = mediator)
        }

        /**
         * Returns default implementation of MessageViewModel
         * This implementation allows to load a data from both database and network.
         *
         * @param id ID of the Channel
         *
         * @return ViewModel instance
         */
        @Deprecated(
            message = "This method is no longer supported. Please pass ChannelId directly to the corresponding methods.",
            replaceWith = ReplaceWith("defaultWithMediator()"),
            level = DeprecationLevel.ERROR,
        )
        @Composable
        fun defaultWithMediator(@Suppress("UNUSED_PARAMETER") id: ChannelId): MessageViewModel = defaultWithMediator()


    }

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private fun Timetoken.formatDate(): String =
        dateFormat.format(this.seconds).lowercase(Locale.getDefault())

    /**
     * Get message with provided ID
     *
     * @param id Message ID
     * @return Message UI Data
     */
    suspend fun get(id: MessageId): MessageUi.Data? =
        messageRepository.get(id)?.toUi()

    /**
     * Get Messages for selected Channel
     * @param filter Room filter query
     * @param contentType Type of the message content. When not null, returned list contains only
     *              messages with passed type. Otherwise all the Messages are returned.
     *              Null by default.
     * @param sorted Array of Sorted objects, result will be sorted by it. Default is descending by timetoken.
     * @param transform Transformer for a Paging Data
     *
     * @return Flow of Message UI Paging Data
     */
    fun getAll(
        channelId: ChannelId,
        filter: Query? = null,
        contentType: String? = null,
        sorted: Array<Sorted> = arrayOf(
            Sorted(
                MessageUi.Data::timetoken.name,
                Sorted.Direction.DESC
            )
        ),
        transform: PagingData<MessageUi>.() -> PagingData<MessageUi> = { this },
    ): Flow<PagingData<MessageUi>> =
        Pager(
            config = config,
            pagingSourceFactory = {
                messageRepository.getAll(
                    id = channelId,
                    contentType = contentType,
                    filter = filter,
                    sorted = sorted,
                )
            },
            remoteMediator = remoteMediator?.apply { setChannel(channelId) }, // awful workaround but currently there's no way to combine composable, pager and vm's
        ).flow.map { paging ->
            paging.map { it.toMessageUi() }
                .transform()
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
    fun removeAll(channelId: ChannelId) = viewModelScope.launch { messageRepository.removeAll(channelId) }

    /**
     * Copy the content of the message to the clipboard
     */
    fun copy(content: AnnotatedString) {
        clipboard.setText(content)
    }

    private fun MessageUi.toDb(): DBMessageWithActions = uiMapper.map(this as MessageUi.Data)
    private fun DBMessageWithActions.toUi(): MessageUi.Data = dbMapper.map(this)
    private fun DBMessageWithActions.toMessageUi(): MessageUi = this.toUi()
    private fun List<DBMessageWithActions>.toUi(): List<MessageUi.Data> = map { it.toUi() }

}