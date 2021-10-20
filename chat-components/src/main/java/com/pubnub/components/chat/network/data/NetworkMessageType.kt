package com.pubnub.components.chat.network.data

import androidx.annotation.StringDef

@StringDef(
    value = [
        NetworkMessage.Type.DEFAULT,
        NetworkMessage.Type.REPLY,
        NetworkMessage.Type.EPHEMERAL,
        NetworkMessage.Type.ERROR,
        NetworkMessage.Type.SYSTEM,
        NetworkMessage.Type.CUSTOM,
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class NetworkMessageType
