package com.pubnub.components.chat.ui.component.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pubnub.components.chat.provider.PubNubPreview
import com.pubnub.components.chat.ui.component.channel.renderer.DefaultChannelRenderer.ChannelItemPlaceholderView
import com.pubnub.components.chat.ui.component.channel.renderer.DefaultChannelRenderer.ChannelItemView

@Composable
@Preview
private fun ChannelItemViewPreview() {
    PubNubPreview {
        ChannelItemView(
            title = "Company Culture",
            description = "Company culture space",
            iconUrl = "https://www.gravatar.com/avatar/ce466f2e445c38976168ba78e46?s=256&d=identicon",
            clickAction = {},
        )
    }
}


@Preview
@Composable
private fun ChannelItemPlaceholder() {
    PubNubPreview {
        ChannelItemPlaceholderView()
    }
}