package com.pubnub.components.chat.ui.component.message.reaction

import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken

data class PickedReaction(
    val userId: UserId,
    val messageTimetoken: Timetoken,
    val type: String,
    val value: String,
)
