package com.pubnub.components.chat.viewmodel.message

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.coroutines.launch

/**
 * [ReactionViewModel] contains the logic for adding and removing message reactions.
 */
class ReactionViewModel constructor(
    private val messageActionRepository: MessageActionRepository<DBMessageAction>,
    private val messageReactionService: DefaultMessageReactionService?,
    private val logger: Logger,
) : ViewModel() {

    companion object {
        /**
         * Returns default implementation of ReactionViewModel
         *
         * @return ViewModel instance
         */
        @Composable
        fun default(): ReactionViewModel {
            val factory = ReactionViewModelFactory(
                messageActionRepository = LocalMessageActionRepository.current,
                messageReactionService = LocalMessageReactionService.current as DefaultMessageReactionService,
                logger = LocalLogger.current,
            )
            return viewModel(factory = factory)
        }

        /**
         * Returns default implementation of ReactionViewModel
         *
         * @param id ID of the Channel
         * @return ViewModel instance
         */
        @Deprecated(
            message = "This method is no longer supported. Please pass ChannelId directly to the [bind] method.",
            replaceWith = ReplaceWith("default()"),
            level = DeprecationLevel.ERROR,
        )
        @Composable
        fun default(
            @Suppress("UNUSED_PARAMETER") id: ChannelId,
        ): ReactionViewModel = default()
    }

    private lateinit var channelId: ChannelId

    init {
        logger.i("Message Reaction VM Init $this")
    }

    fun bind(channelId: ChannelId, types: Array<String> = arrayOf("reaction")){
        messageReactionService?.bind(types)
        synchronize(channelId)
    }

    fun unbind(){
        messageReactionService?.unbind()
    }

    /**
     * Adds or removes the selected reaction
     * Will update state in PubNub and local repository
     *
     * @param react Selected reaction for the message
     */
    fun reactionSelected(react: React) {
        logger.i("Reaction selected: '$react'")
        viewModelScope.launch {
            logger.v("Looking for reaction: '$react'")
            val storedReaction = messageActionRepository.get(
                react.message.publisher.id,
                react.message.channel,
                react.message.timetoken,
                react.reaction.type,
                react.reaction.value,
            )

            if (storedReaction?.user == react.message.publisher.id) {
                logger.v("Removing action: '$storedReaction")
                messageReactionService?.remove(
                    storedReaction.channel,
                    storedReaction.messageTimestamp,
                    storedReaction.published,
                    storedReaction.type,
                    storedReaction.value
                )
            } else {
                logger.v("Adding action: '$react'")
                messageReactionService?.add(
                    react.message.channel,
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
    private fun synchronize(channelId: ChannelId) {
        if (messageReactionService == null) return
        viewModelScope.launch(Dispatchers.IO) {
            messageReactionService.synchronize(channelId)
        }
    }
}
