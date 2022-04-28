package com.pubnub.components.chat.ui.component.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.message.reaction.UnicodeEmoji

object MenuDefaults {
    @Composable
    fun items(message: MessageUi.Data) = listOf(
        MenuItemState(
            title = R.string.menu_copy,
            iconPainter = rememberVectorPainter(image = Icons.Rounded.ContentCopy),
            action = Copy(message),
        ),
    )

    fun reactions() = listOf(
        UnicodeEmoji("\uD83D\uDC4D"),    // 👍 thumbs up
        UnicodeEmoji("\u2764"),          // ❤ red heart U+2764
        UnicodeEmoji("\uD83D\uDE02"),    // 😂 face with tears of joy U+1F602
        UnicodeEmoji("\uD83D\uDE32"),    // 😲 astonished face U+1F632
        UnicodeEmoji("\uD83D\uDE22"),    // 😢 crying face U+1F622
        UnicodeEmoji("\uD83D\uDD25"),    // 🔥 fire U+1F525
    )
}