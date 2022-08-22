package com.pubnub.components.chat.ui.component.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.message.renderer.GroupMessageRenderer.ThemedText

@Composable
fun MenuItem(
    state: MenuItemState,
    onClick: ((MenuAction) -> Unit)? = null,
    theme: MenuItemTheme = LocalMenuItemTheme.current,
) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = { onClick?.invoke(state.action) },
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
            )
            .then(theme.modifier),
        horizontalArrangement = theme.horizontalArrangement,
        verticalAlignment = theme.verticalAlignment,
    ) {
        Icon(
            modifier = theme.icon.modifier,
            painter = state.iconPainter,
            contentDescription = stringResource(id = state.title),
            tint = theme.icon.tint,
        )

        ThemedText(
            text = stringResource(id = state.title),
            theme = theme.text,
            modifier = theme.modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuItemPreview() {
    val message: MessageUi.Data = dummyMessageData()
    MenuItemTheme(DefaultMenuItemTheme) {
        MenuItem(
            MenuItemState(
                title = R.string.menu_copy,
                iconPainter = rememberVectorPainter(image = Icons.Rounded.ContentCopy),
                action = Copy(message),
            )
        )
    }
}

internal fun dummyMessageData() = MessageUi.Data(
    uuid = "uuid",
    publisher = MemberUi.Data("user-id", "Publisher"),
    channel = "channel",
    text = "",
    createdAt = "IsoDate",
    timetoken = 0L,
    isSending = false,
    isDelivered = false,
    reactions = emptyList(),
)