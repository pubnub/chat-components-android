package com.pubnub.components.chat.network.mapper

import com.pubnub.components.chat.network.data.NetworkHistoryMessage
import com.pubnub.components.data.message.action.DBMessageAction
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.mapper.MapperWithId

class NetworkMessageActionHistoryMapper :
    MapperWithId<NetworkHistoryMessage, Array<DBMessageAction>> {
    override fun map(id: ChannelId, input: NetworkHistoryMessage): Array<DBMessageAction> {
        return input.messageActions.map { event ->
                    DBMessageAction(
                        channel = id,
                        user = event.uuid!!,
                        messageTimestamp = event.messageTimetoken,
                        published = event.actionTimetoken!!.toLong(),
                        type = event.type,
                        value = event.value,
                    )

        }.toTypedArray()
    }

}
