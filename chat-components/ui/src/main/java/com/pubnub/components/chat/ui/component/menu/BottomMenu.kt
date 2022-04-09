package com.pubnub.components.chat.ui.component.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.message.MessageUi

@Composable
fun BottomMenu(
    message: MessageUi.Data,
    states: List<MenuItemState>,
    onAction: (MenuAction) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit = {
        Text("Reactions placeholder for ${message.uuid}")
    },
    content: @Composable ColumnScope.() -> Unit = {
        MessageMenu(items = states, onClick = { onAction(it) })
    },
){
    Dialog(
        onDismissRequest = {},
    ){
        Card(modifier = modifier) {
            Column {
                header()
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomMenuPreview(){
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
    MenuItemTheme(DefaultMenuItemTheme){
        BottomMenu(
            message = dummyMessageData(),
            states = items,
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}