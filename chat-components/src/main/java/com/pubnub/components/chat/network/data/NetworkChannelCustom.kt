package com.pubnub.components.chat.network.data

import androidx.annotation.Keep

@Keep
data class NetworkChannelCustom(
    val type: String = "default",
    val profileUrl: String,
)