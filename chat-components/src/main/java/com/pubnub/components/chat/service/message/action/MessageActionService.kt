package com.pubnub.components.chat.service.message.action

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.data.message.action.MessageAction
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.util.Timetoken

interface MessageActionService<IN: MessageAction> {
    // region Lifecycle
    fun bind(types: Array<String> = arrayOf("reaction"))
    fun unbind()
    // endregion

    // region Synchronization
    fun synchronize(channel: ChannelId, lastTimetoken: Timetoken? = null)
    // endregion

    // region Repository
//    suspend fun insert(vararg action: IN)
    suspend fun add(channel: ChannelId, messageTimetoken: Timetoken, type: String, value: String)
    suspend fun remove(channel: ChannelId, messageTimetoken: Timetoken, published: Timetoken, type: String, value: String)
    // endregion
}

val LocalMessageActionService =
    staticCompositionLocalOf<MessageActionService<*>> { throw MessageActionServiceNotInitializedException() }

class MessageActionServiceNotInitializedException : Exception("MessageAction Service not initialized")