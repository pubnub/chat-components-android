package com.pubnub.components.chat.network.data

import com.pubnub.api.models.consumer.PNBoundedPage
import com.pubnub.framework.util.Timetoken

data class NetworkHistorySyncResult(
    val minTimetoken: Timetoken?,
    val maxTimetoken: Timetoken?,
    val page: PNBoundedPage?,
    val messageCount: Int,
)
