package com.pubnub.components.chat.ui.component.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.message.reaction.DefaultReactionTheme
import com.pubnub.components.chat.ui.component.message.reaction.ReactionTheme
import com.pubnub.components.chat.ui.component.message.reaction.renderer.DefaultReactionsPickerRenderer

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomMenu(
    message: MessageUi.Data,
    states: List<MenuItemState>,
    onAction: (MenuAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit = {
        DefaultReactionsPickerRenderer.ReactionsPicker { reaction -> onAction(React(reaction, message)) }
    },
    content: @Composable ColumnScope.() -> Unit = {
        MessageMenu(items = states, onClick = { onAction(it) })
    },
){
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth=false)
    ){
        BottomMenuContent(message, states, onAction, onDismiss, modifier, header, content)
    }
}

@Composable
private fun BottomMenuContent(
    message: MessageUi.Data,
    states: List<MenuItemState>,
    onAction: (MenuAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit = {
        DefaultReactionsPickerRenderer.ReactionsPicker { reaction -> onAction(React(reaction, message)) }
    },
    content: @Composable ColumnScope.() -> Unit = {
        MessageMenu(items = states, onClick = { onAction(it) })
    },
){
    Box(Modifier.fillMaxSize().clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onDismiss,
    )) {
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
    ReactionTheme(DefaultReactionTheme) {
        MenuItemTheme(DefaultMenuItemTheme) {
            BottomMenuContent(
                message = dummyMessageData(),
                states = items,
                onAction = {},
                onDismiss = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}