package com.pubnub.components.chat.network.mapper

import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.framework.mapper.Mapper

class NetworkMessageActionMapper: Mapper<PNMessageActionResult, DBMessageAction> {
    override fun map(input: PNMessageActionResult): DBMessageAction =
        DBMessageAction(
            value = input.messageAction.value,
            type = input.messageAction.type,
            user = input.messageAction.uuid!!,
            channel = input.channel,
            messageTimestamp = input.messageAction.messageTimetoken,
            published = input.messageAction.actionTimetoken!!,
        )
}
