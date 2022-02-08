package com.pubnub.components.chat.network.mapper

import com.pubnub.api.models.consumer.history.PNFetchMessageItem
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.Mapper

class NetworkMessageActionHistoryMapper : Mapper<PNFetchMessageItem, Array<DBMessageAction>> {
    lateinit var channel: ChannelId

    override fun map(input: PNFetchMessageItem): Array<DBMessageAction> {
        return input.actions?.flatMap { (type, list) ->
            list.flatMap { (action, users) ->
                users.map { user ->
                    DBMessageAction(
                        channel = channel,
                        user = user.uuid,
                        messageTimestamp = input.timetoken,
                        published = user.actionTimetoken.toLong(),
                        type = type,
                        value = action,
                    )
                }
            }
        }?.toTypedArray() ?: emptyArray()
    }

}
