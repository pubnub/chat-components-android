package com.pubnub.components.chat.ui.component.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.pubnub.components.chat.ui.R

@Composable
fun MessageMenu(
    items: List<MenuItemState>,
    onClick: (MenuAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(MenuItemState) -> Unit = { state ->
        MenuItem(
            state = state,
            onClick = onClick,
        )
    }
) {
    Column(modifier) {
        items.forEach { state ->
            content(state)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageMenuPreview() {
    val items = listOf(
        MenuItemState(
            title = R.string.menu_copy,
            iconPainter = rememberVectorPainter(image = Icons.Rounded.ContentCopy),
            action = Copy(dummyMessageData()),
        ),
        MenuItemState(
            title = R.string.menu_delete,
            iconPainter = rememberVectorPainter(image = Icons.Rounded.Delete),
            action = Delete(dummyMessageData()),
        ),
    )
    MenuItemTheme(DefaultMenuItemTheme) {
        MessageMenu(items = items, onClick = {})
    }
}
