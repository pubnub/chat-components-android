package com.pubnub.components.chat.viewmodel.message

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import com.pubnub.components.chat.provider.LocalLogger
import com.pubnub.components.chat.provider.LocalMessageActionRepository
import com.pubnub.components.chat.service.message.action.DefaultMessageReactionService
import com.pubnub.components.chat.service.message.action.LocalMessageReactionService
import com.pubnub.components.chat.ui.component.menu.React
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.component.provider.LocalUser
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.components.repository.message.action.MessageActionRepository
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.service.error.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

/**
 * [ReactionViewModel] contains the logic for adding and removing message reactions.
 */
@OptIn(ExperimentalPagingApi::class, FlowPreview::class)
class ReactionViewModel constructor(
    private val userId: UserId,
    private val channelId: ChannelId,
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val messageReactionService: DefaultMessageReactionService?,
    private val logger: Logger,
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
                userId = LocalUser.current,
                channelId = id,
                messageActionRepository = LocalMessageActionRepository.current,
                messageReactionService = LocalMessageReactionService.current as DefaultMessageReactionService,
                logger = LocalLogger.current,
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
     * @param react Selected reaction for the message
     */
    fun reactionSelected(react: React) {
        logger.i("Reaction selected: %s", react.toString())
        viewModelScope.launch {
            logger.v("Looking for reaction: %s", react.toString())
            val storedReaction = messageActionRepository.get(
                userId,
                channelId,
                react.message.timetoken,
                react.reaction.type,
                react.reaction.value,
            )

            if (storedReaction?.user == userId) {
                logger.v("Removing action: %s", storedReaction.toString())
                messageReactionService?.remove(
                    storedReaction.channel,
                    storedReaction.messageTimestamp,
                    storedReaction.published,
                    storedReaction.type,
                    storedReaction.value
                )
            } else {
                logger.v("Adding action: %s", react.toString())
                messageReactionService?.add(
                    channelId,
                    react.message.timetoken,
                    react.reaction.type,
                    react.reaction.value,
                )
            }
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
