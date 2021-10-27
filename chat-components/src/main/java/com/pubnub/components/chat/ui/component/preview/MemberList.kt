package com.pubnub.components.chat.ui.component.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pubnub.components.chat.provider.PubNubPreview
import com.pubnub.components.chat.ui.component.member.renderer.DefaultMemberRenderer.MemberItemPlaceholderView
import com.pubnub.components.chat.ui.component.member.renderer.DefaultMemberRenderer.MemberItemView


@Composable
@Preview
private fun MemberItemViewPreview() {
    PubNubPreview {
        MemberItemView(
            name = "Mark Kelley (You)",
            description = "Office Assistant",
            profileUrl = "https://randomuser.me/api/portraits/men/1.jpg",
            clickAction = {},
        )
    }
}

@Composable
@Preview
private fun MemberItemViewPreviewPreview() {
    PubNubPreview {
        MemberItemPlaceholderView()
    }
}

