package com.pubnub.components.chat.viewmodel.channel

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.*
import com.pubnub.components.R
import com.pubnub.components.chat.provider.LocalChannelRepository
import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.component.provider.LocalUser
import com.pubnub.components.chat.ui.mapper.channel.DBChannelMapper
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.channel.ChannelRepository
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
    private val id: UserId,
    private val repository: ChannelRepository<DBChannel, DBChannelWithMembers>,
    private val resources: Resources,
    private val dbMapper: Mapper<DBChannelWithMembers, ChannelUi.Data> = DBChannelMapper(),
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of ChannelViewModel
         *
         * @param resources Context resources
         * @param id Current User Id
         * @param repository Channel Repository implementation
         * @param dbMapper Database object to UI object mapper
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            resources: Resources,
            id: UserId = LocalUser.current,
            repository: ChannelRepository<DBChannel, DBChannelWithMembers> = LocalChannelRepository.current,
            dbMapper: Mapper<DBChannelWithMembers, ChannelUi.Data> = DBChannelMapper(),
        ): ChannelViewModel {
            val channelFactory = ChannelViewModelFactory(id, repository, resources, dbMapper)
            return viewModel(factory = channelFactory)
        }
    }

    /**
     * Get channel by ID
     *
     * @param id ID of the channel
     * @return ChannelUi.Data representation if channel exists, null otherwise
     */
    fun get(id: ChannelId): ChannelUi.Data? =
        runBlocking {
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                repository.get(id)?.let { dbMapper.map(it) }
            }
        }

    /**
     * Get paged list of channels
     * Channels are grouped by type and extra separator item is placed,
     * @see [ChannelUi.Data.GROUP], [ChannelUi.Data.DIRECT], [ChannelUi.Data.DEFAULT]
     *
     * @param filter Query filter for database
     * @param sorted Array of Sorted objects
     * @param transform Transformer for a Paging Data
     * @return Flow of Channel UI Paging Data
     */
    fun getAll(
        filter: Query? = null,
        sorted: Array<Sorted> = emptyArray(),
        transform: PagingData<ChannelUi>.() -> PagingData<ChannelUi> = { this },
    ): Flow<PagingData<ChannelUi>> =
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory = {
                repository.getAll(
                    id = id,
                    filter = filter,
                    sorted = sorted,
                )
            },
        ).flow.map { pagingData ->
            pagingData
                .map { it.toUi() }
                .transform()
        }.distinctUntilChanged().cachedIn(viewModelScope)

    /**
     * Get map of grouped Channels
     * Channels are grouped by type and extra separator item is placed,
     * @see [ChannelUi.Data.GROUP], [ChannelUi.Data.DIRECT], [ChannelUi.Data.DEFAULT]
     *
     * @return Map of group name and list of Channels
     */
    fun getList(): Map<String?, List<ChannelUi>> {
        val channels =
            runBlocking {
                withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                    repository.getList().map { it.toUi() }
                }
            }
        return mapOf(
            resources.getString(R.string.group_title) to channels.filter { it is ChannelUi.Data && it.type == ChannelUi.Data.GROUP },
            resources.getString(R.string.default_title) to channels.filter { it is ChannelUi.Data && it.type == ChannelUi.Data.DEFAULT },
            resources.getString(R.string.direct_title) to channels.filter { it is ChannelUi.Data && it.type == ChannelUi.Data.DIRECT },
        )
    }

    private fun DBChannelWithMembers.toUi(): ChannelUi = dbMapper.map(this)
}
