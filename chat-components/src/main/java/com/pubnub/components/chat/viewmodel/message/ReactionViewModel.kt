package com.pubnub.components.chat.viewmodel.message

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import com.pubnub.components.chat.provider.LocalMessageActionRepository
import com.pubnub.components.chat.service.message.action.DefaultMessageReactionService
import com.pubnub.components.chat.service.message.action.LocalMessageReactionService
import com.pubnub.components.chat.ui.component.message.reaction.PickedReaction
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * [ReactionViewModel] contains the logic for adding and removing message reactions.
 */
@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class ReactionViewModel constructor(
    private val channelId: ChannelId,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val messageReactionService: DefaultMessageReactionService?,
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of ReactionViewModel
         *
         * @param id ID of the Channel
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(
            id: ChannelId = LocalChannel.current,
        ): ReactionViewModel {
            val factory = ReactionViewModelFactory(
                channelId = id,
                messageActionRepository = LocalMessageActionRepository.current,
                messageReactionService = LocalMessageReactionService.current as DefaultMessageReactionService,
            )
            return viewModel(factory = factory)
        }
    }

    init {
        synchronize()
    }

    /**
     * Adds or removes the selected reaction
     * Will update state in PubNub and local repository
     *
     * @param reaction Selected reaction for the message
     */
    fun reactionSelected(reaction: PickedReaction) {
        Timber.e("On Reaction: $reaction")
        viewModelScope.launch {
            Timber.e("Looking for reaction '$reaction' ")
            val storedReaction = messageActionRepository.get(
                reaction.userId,
                channelId,
                reaction.messageTimetoken,
                reaction.type,
                reaction.value,
            )

            Timber.e("Stored action: $storedReaction")
            if (storedReaction?.user == reaction.userId)
                messageReactionService?.remove(
                    storedReaction.channel,
                    storedReaction.messageTimestamp,
                    storedReaction.published,
                    storedReaction.type,
                    storedReaction.value
                )
            else
                messageReactionService?.add(
                    channelId,
                    reaction.messageTimetoken,
                    reaction.type,
                    reaction.value,
                )
        }
    }

    /**
     * Synchronize message reactions for current channel
     */
    private fun synchronize() {
        if (messageReactionService == null) return
        viewModelScope.launch(Dispatchers.IO) {
            messageReactionService.synchronize(channelId)
        }
    }
}
