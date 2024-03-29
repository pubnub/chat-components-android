package com.pubnub.components.chat.viewmodel.member

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.pubnub.components.chat.provider.LocalMemberRepository
import com.pubnub.components.chat.service.channel.LocalOccupancyService
import com.pubnub.components.chat.service.channel.OccupancyService
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalUser
import com.pubnub.components.chat.ui.mapper.member.DBMemberMapper
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.repository.member.MemberRepository
import com.pubnub.components.repository.util.Query
import com.pubnub.components.repository.util.Sorted
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * [MemberViewModel] contains the logic for getting the list of members from the repository.
 * The returned object is mapped to UI data and contains only the data needed to be displayed.
 */
class MemberViewModel constructor(
    private val userId: UserId,
    private val repository: MemberRepository<DBMember, DBMemberWithChannels>,
    private val presenceService: OccupancyService?,
    private val dbMapper: Mapper<DBMemberWithChannels, MemberUi.Data>,
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of MemberViewModel
         *
         * @param pubNub PubNub instance
         * @param repository Member Repository implementation
         * @param occupancyService Occupancy Service implementation
         * @param dbMapper Database object to UI object mapper
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            userId: UserId = LocalUser.current,
            repository: MemberRepository<DBMember, DBMemberWithChannels> = LocalMemberRepository.current,
            occupancyService: OccupancyService = LocalOccupancyService.current,
            dbMapper: Mapper<DBMemberWithChannels, MemberUi.Data> = DBMemberMapper(),
        ): MemberViewModel {
            val memberFactory =
                MemberViewModelFactory(userId, repository, occupancyService, dbMapper)
            return viewModel(factory = memberFactory)
        }
    }

    /**
     * Get Member by ID
     *
     * @param userId ID of the User, default is current user ID
     * @return MemberUi.Data representation if Member exists, or null
     */
    fun getMember(userId: UserId = this.userId): MemberUi.Data? =
        runBlocking { repository.get(userId)?.let { dbMapper.map(it) } }

    /**
     * Get paged list of Members
     *
     * @param channelId Id of the Channel
     * @param filter Query filter for database
     * @param sorted Array of Sorted objects
     * @param transform Transformer for a Paging Data
     *
     * Note: when [channelId] is not set, then Members for all channels are returned
     * @return Flow of Member UI Paging Data
     */
    @Suppress("UNCHECKED_CAST")
    fun getAll(
        channelId: ChannelId? = null,
        filter: Query? = null,
        sorted: Array<Sorted> = arrayOf(Sorted(MemberUi.Data::name.name, Sorted.Direction.ASC)),
        transform: PagingData<MemberUi>.() -> PagingData<MemberUi> = { this },
    ): Flow<PagingData<MemberUi>> =
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory =
            {
                repository.getAll(
                    id = channelId,
                    filter = filter,
                    sorted = sorted,
                )
            },
        ).flow.map { (it.map { dbMapper.map(it) } as PagingData<MemberUi>).transform() }

    /**
     * Get list of Members
     *
     * @param channelId Id of the Channel
     * Note: when [channelId] is not set, then Members for all channels are returned
     * @return List of Member UI Data
     */
    fun getList(channelId: ChannelId? = null): List<MemberUi.Data> =
        runBlocking {
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                channelId?.let { repository.getList(channelId).map { dbMapper.map(it) } }
                    ?: repository.getList().map { dbMapper.map(it) }
            }
        }

    /**
     * Get list of Members, grouped by first letter of name
     *
     * @param channelId Id of the Channel
     * Note: when [channelId] is not set, then Members for all channels are returned
     * @return Map of first letter and list of Member UI Data
     */
    fun getListGroup(channelId: ChannelId? = null): Map<String?, List<MemberUi.Data>> =
        getList(channelId).groupBy { (it.name ?: it.id)[0].toString() }

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
}
