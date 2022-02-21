package com.pubnub.components.chat.ui.component.message.reaction

import androidx.annotation.DrawableRes

sealed interface Reaction {
    val type: String
    val value: String
}

sealed class Emoji : Reaction

data class UnicodeEmoji(override val type: String, override val value: String) : Emoji() {
    constructor(value: String) : this("reaction", value)
}

data class LocalResourceEmoji(
    override val type: String,
    override val value: String,
    @DrawableRes val drawable: Int
) : Emoji() {
    constructor(value: String, drawable: Int) : this("reaction_resource", value, drawable)
}
