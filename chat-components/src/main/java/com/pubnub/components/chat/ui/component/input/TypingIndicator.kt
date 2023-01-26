package com.pubnub.components.chat.ui.component.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pubnub.components.chat.provider.LocalMemberFormatter
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.mapper.typing.DomainTypingMapper
import com.pubnub.framework.service.LocalTypingService

@Composable
fun TypingIndicator(
    typingIndicatorContent: @Composable ColumnScope.(List<TypingUi>) -> Unit = { typing ->
        TypingIndicatorContent(typing)
    }
) {
    Column {
        checkNotNull(LocalChannel.current)

        val channel = LocalChannel.current
        val typingService = LocalTypingService.current
        val typingMapper = DomainTypingMapper(LocalMemberFormatter.current)

        DisposableEffect(channel) {
            typingService.bind(channel)
            onDispose { typingService.unbind() }
        }

        val typing by typingService.getTyping(channel).collectAsState(initial = emptyList())
        typingIndicatorContent(typing.map { typingMapper.map(it) })
    }
}
