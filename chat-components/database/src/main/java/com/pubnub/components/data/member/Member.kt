package com.pubnub.components.data.member

import com.pubnub.framework.data.UserId

interface Member {
    val id: UserId              // used as an unique user identifier across the components
    val name: String            // displayed on Messages, Typing Indicator and Member List
    val email: String?          // not used in Chat Components
    val externalId: String?     // not used in Chat Components
    val profileUrl: String?     // expects an image URL to display the user avatar

    val custom: Any?            // can store additional data
    val eTag: String?            // not used in Chat Components
    val updated: String?         // not used in Chat Components
}
