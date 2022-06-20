package com.pubnub.components.data.message

import com.pubnub.framework.data.MessageId

interface Message {
    val id: MessageId           // The unique identifier for a message. UUID v4 algorithm should be used here
    val text: String            // Text of the message to be displayed in the UI
    val contentType: String?    // If message contains any extra content, this field describes its type, ["externalUrl", "imageUrl"]
    val content: Any?           // Extra content for the message. Can contain any feature-specific data, e.g. URLs to external images.
    val createdAt: String       // ISO8601 date string of when the message was created
    val custom: Any?            // Any additional payload possibly needed and provided by end users
}
