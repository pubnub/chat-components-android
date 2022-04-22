package com.pubnub.components.chat.viewmodel.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.pubnub.api.PubNub
import com.pubnub.components.chat.service.channel.OccupancyService
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.member.DBMemberWithChannels
import com.pubnub.components.repository.member.MemberRepository
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class MemberViewModelFactory(
    private val pubNub: PubNub,
    private val userId: UserId,
    private val repository: MemberRepository<DBMember, DBMemberWithChannels>,
    private val presenceService: OccupancyService?,
    private val dbMapper: Mapper<DBMemberWithChannels, MemberUi.Data>,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            return MemberViewModel(pubNub, userId, repository, presenceService, dbMapper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
