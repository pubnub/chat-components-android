package com.pubnub.components.chat.viewmodel.channel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.mapper.channel.DBChannelMapper
import com.pubnub.components.data.channel.DBChannel
import com.pubnub.components.data.channel.DBChannelWithMembers
import com.pubnub.components.repository.channel.ChannelRepository
import com.pubnub.components.repository.channel.DefaultChannelRepository
import com.pubnub.framework.data.UserId
import com.pubnub.framework.mapper.Mapper

@OptIn(ExperimentalPagingApi::class)
class ChannelViewModelFactory(
    private val userId: UserId,
    private val repository: ChannelRepository<DBChannel, DBChannelWithMembers>,
    private val resources: Resources,
    private val dbMapper: Mapper<DBChannelWithMembers, ChannelUi.Data> = DBChannelMapper(),
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChannelViewModel::class.java)) {
            return ChannelViewModel(userId, repository, resources, dbMapper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
