package com.pubnub.components.chat.network.mapper

import com.pubnub.components.chat.network.data.NetworkHistoryMessage
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.MapperWithId

class NetworkMessageActionHistoryMapper :
    MapperWithId<NetworkHistoryMessage, Array<DBMessageAction>> {
    override fun map(id: ChannelId, input: NetworkHistoryMessage): Array<DBMessageAction> {
        return input.actions?.flatMap { (type, list) ->
            list.flatMap { (action, users) ->
                users.map { user ->
                    DBMessageAction(
                        channel = id,
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
