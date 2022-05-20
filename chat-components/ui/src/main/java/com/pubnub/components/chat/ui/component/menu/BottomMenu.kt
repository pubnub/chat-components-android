package com.pubnub.components.chat.ui.component.menu

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
    message: MessageUi.Data?,
    onAction: (MenuAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    states: List<MenuItemState> = message?.let { MenuDefaults.items(message) } ?: emptyList(),
    headerContent: @Composable ColumnScope.() -> Unit = {},
    bodyContent: @Composable ColumnScope.() -> Unit = {
        MessageMenu(items = states, onClick = { onAction(it) })
    },
) {

    AnimatedVisibility(visible = visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            BottomMenuContent(
                message,
                states,
                onAction,
                onDismiss,
                modifier,
                headerContent,
                bodyContent
            )
        }
    }
}

@SuppressLint("UnnecessaryComposedModifier")
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedVisibilityScope.BottomMenuContent(
    message: MessageUi.Data?,
    states: List<MenuItemState>,
    onAction: (MenuAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    headerContent: @Composable ColumnScope.() -> Unit = {
        DefaultReactionsPickerRenderer.ReactionsPicker { reaction ->
            message?.let { onAction(React(reaction, message)) }
        }
    },
    bodyContent: @Composable ColumnScope.() -> Unit = {
        MessageMenu(items = states, onClick = { onAction(it) })
    },
) {
    Box(
        Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .animateEnterExit(
                    enter = slideInVertically(
                        animationSpec = tween(),
                        initialOffsetY = { it },
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(),
                        targetOffsetY = { it },
                    )
                )
                .composed { modifier },
        ) {
            Column {
                headerContent()
                bodyContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomMenuPreview() {
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
            AnimatedVisibility(visible = true) {
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
}
