package com.pubnub.components.data.member

import com.pubnub.framework.data.UserId

interface Member {
    val id: UserId              // The unique identifier for an user
    val name: String?           // Name of the user that could be displayed in the UI
    val email: String?          // not used in Chat Components
    val externalId: String?     // not used in Chat Components
    val profileUrl: String?     // URL to an user avatar that could be displayed in the UI
    val type: String            // Type of the member
    val status: String?         // Status of the member
    val custom: Any?            // Any additional payload possibly needed and provided by end users
    val eTag: String?           // not used in Chat Components
    val updated: String?        // not used in Chat Components
}
