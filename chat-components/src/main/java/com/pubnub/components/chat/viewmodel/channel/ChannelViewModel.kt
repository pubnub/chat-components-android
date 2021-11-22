package com.pubnub.components.chat.viewmodel.channel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.*
import com.pubnub.components.chat.provider.LocalChannelRepository
import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.component.provider.LocalPubNub
import com.pubnub.components.chat.ui.mapper.channel.DBChannelMapper
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.channel.DefaultChannelRepository
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * [ChannelViewModel] contains the logic for getting the list of channels from the repository.
 * The returned object is mapped to UI data and contains only the data needed to be displayed.
 */
@OptIn(ExperimentalPagingApi::class)
class ChannelViewModel constructor(
    private val userId: UserId,
    private val repository: DefaultChannelRepository,
    private val dbMapper: Mapper<DBChannelWithMembers, ChannelUi.Data> = DBChannelMapper(),
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of ChannelViewModel
         *
         * @param userId Current User Id
         * @param repository Channel Repository implementation
         * @param dbMapper Database object to UI object mapper
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            userId: UserId = LocalPubNub.current.configuration.uuid,
            repository: DefaultChannelRepository = LocalChannelRepository.current,
            dbMapper: Mapper<DBChannelWithMembers, ChannelUi.Data> = DBChannelMapper(),
        ): ChannelViewModel {
            val channelFactory = ChannelViewModelFactory(userId, repository, dbMapper)
            return viewModel(factory = channelFactory)
        }
    }

    /**
     * Get channel by ID
     *
     * @param channelId ID of the channel
     * @return ChannelUi.Data representation if channel exists, null otherwise
     */
    fun get(channelId: ChannelId): ChannelUi.Data? =
        runBlocking {
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                repository.get(channelId)?.let { dbMapper.map(it) }
            }
        }

    /**
     * Get paged list of channels
     * Channels are grouped by type and extra separator item is placed,
     * @see [ChannelUi.Data.GROUP], [ChannelUi.Data.DIRECT], [ChannelUi.Data.DEFAULT]
     *
     * @param filter Query filter for database
     * @param sorted Array of Sorted objects
     *
     * @return Flow of Channel UI Paging Data
     */
    fun getAll(
        filter: Query? = null,
        sorted: Array<Sorted> = emptyArray(),
    ): Flow<PagingData<ChannelUi>> =
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory = {
                repository.getAll(
                    userId = userId,
                    filter = filter,
                    sorted = sorted,
                )
            },
        ).flow.map { pagingData ->
            pagingData
                .map { it.toUi() }
                .insertSeparators { before: ChannelUi?, after: ChannelUi? ->
                    val isHeader = before is ChannelUi.Header || after is ChannelUi.Header
                    if (isHeader) return@insertSeparators null

                    val firstOne = before == null && after != null
                    val typeChanged =
                        (before != null && after != null && (before as ChannelUi.Data).type != (after as ChannelUi.Data).type)
                    if (firstOne || typeChanged) {
                        val title = when ((after as ChannelUi.Data).type) {
                            ChannelUi.Data.GROUP -> "Channels"
                            ChannelUi.Data.DIRECT -> "Direct Chats"
                            else -> "Channels"
                        }

                        ChannelUi.Header(title)
                    } else null
                }
        }.distinctUntilChanged().cachedIn(viewModelScope)

    /**
     * Get map of grouped Channels
     * Channels are grouped by type and extra separator item is placed,
     * @see [ChannelUi.Data.GROUP], [ChannelUi.Data.DIRECT], [ChannelUi.Data.DEFAULT]
     *
     * @return Map of group name and list of Channels
     */
    fun getList(): Map<String?, List<ChannelUi.Data>> {
        val channels =
            runBlocking {
                withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    repository.getList().map { it.toUi() }
                }
            }
        return mapOf(
            "Group" to channels.filter { it.type == ChannelUi.Data.GROUP },
            "Default" to channels.filter { it.type == ChannelUi.Data.DEFAULT },
            "Direct" to channels.filter { it.type == ChannelUi.Data.DIRECT },
        )
    }

    private fun DBChannelWithMembers.toUi(): ChannelUi.Data = dbMapper.map(this)
}
