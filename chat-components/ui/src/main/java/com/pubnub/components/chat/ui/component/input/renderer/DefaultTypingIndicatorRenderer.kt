package com.pubnub.components.chat.ui.component.input.renderer

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.input.LocalTypingIndicatorTheme
import com.pubnub.framework.data.Typing

object DefaultTypingIndicatorRenderer : TypingIndicatorRenderer {

    @Composable
    override fun TypingIndicator(data: List<Typing>) {
        val context = LocalContext.current
        val theme = LocalTypingIndicatorTheme.current

        val lastData = data.filter { it.isTyping }.maxByOrNull { it.timestamp }

        Row(
            modifier = theme.modifier.semantics {
                contentDescription = context.getString(R.string.typing_indicator)
            },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (lastData?.isTyping != null) {
                // Draw icon
                theme.icon.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = LocalContext.current.resources.getString(R.string.typing_description),
                        modifier = Modifier
                            .clip(theme.icon.shape)
                            .then(theme.icon.modifier),
                        tint = theme.icon.tint,
                    )
                }
                // Draw text
                Text(
                    text = context.getString(R.string.is_typing, lastData.userId),
                    fontWeight = theme.text.fontWeight,
                    fontSize = theme.text.fontSize,
                    color = theme.text.color,
                    overflow = theme.text.overflow,
                    maxLines = theme.text.maxLines,
                    modifier = theme.text.modifier,
                )
            }
        }
    }

}