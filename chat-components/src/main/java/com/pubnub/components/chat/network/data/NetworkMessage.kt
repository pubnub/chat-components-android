package com.pubnub.components.chat.network.data

import java.util.*

data class NetworkMessage(
    val id: String = UUID.randomUUID().toString(),
    @NetworkMessageType val type: String? = Type.DEFAULT,
    val text: String?,
    val attachment: List<Attachment>? = null,
    val custom: Any? = null,
) {

    abstract class Attachment(open val type: String) {
        data class Image(
            val imageUrl: String,
            val custom: Any? = null,
        ) : Attachment("image")

        data class Link(
            val link: String,
            val custom: Any? = null,
        ) : Attachment("link")

        data class Custom(val custom: Any?) : Attachment("custom")
    }

    /**
     *  The root type should be an enum that defines overall scope (standard vs reply) and not literal type (text, image, etc).
     */
    object Type {
        /** A regular message that is sent */
        const val DEFAULT = "default"

        /** A reply to a regular message, typically threaded in the UI */
        const val REPLY = "reply"

        /** A message not persisted in History (PubNub) nor locally (Non-memory datastore) */
        const val EPHEMERAL = "ephemeral"

        /** Alerting that an error occurred */
        const val ERROR = "error"

        /** An auto-generated message from some system or non-user moderator */
        const val SYSTEM = "system"

        /** Whatever the user wants to define can be placed here and they can determine what to do */
        const val CUSTOM = "custom"
    }
}

