package com.pubnub.components.chat.ui.component.message.reaction.renderer

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.common.ButtonTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.message.reaction.LocalReactionTheme
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.framework.data.UserId

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
object DefaultReactionPickerRenderer : ReactionPickerRenderer {

    private val mapping: EmojiMap = mapOf(
        "emoji_1" to R.drawable.emoji_1,
        "emoji_2" to R.drawable.emoji_2,
        "emoji_3" to R.drawable.emoji_3,
        "emoji_4" to R.drawable.emoji_4,
        "emoji_5" to R.drawable.emoji_5,
        "emoji_6" to R.drawable.emoji_6,
    )

    @Composable
    override fun Dialog(
        scope: AnimatedVisibilityScope?,
        onSelected: (String) -> Unit,
        onClose: () -> Unit,
    ) {
        ReactionsDialog(
            scope = scope,
            onSelected = onSelected,
            onClose = onClose,
        )
    }

    @Composable
    override fun Selector(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (String) -> Unit,
        onAdd: () -> Unit,
    ) {
        MessageReactions(
            currentUserId = currentUserId,
            reactions = reactions,
            onSelected = onSelected,
            onAdd = onAdd,
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun ReactionsDialog(
        scope: AnimatedVisibilityScope?,
        onSelected: (String) -> Unit,
        onClose: () -> Unit,
    ) {
        val theme = LocalReactionTheme.current

        Dialog(
            onDismissRequest = onClose,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val animateModifier = scope?.let {
                with(it) {
                    Modifier.animateEnterExit(
                        enter = expandHorizontally(
                            expandFrom = Alignment.CenterHorizontally,
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        ),
                        exit = shrinkHorizontally(
                            shrinkTowards = Alignment.CenterHorizontally,
                            animationSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    )
                }
            } ?: Modifier
            Card(
                shape = theme.dialogShape.shape,
                backgroundColor = theme.dialogShape.tint,
                modifier = theme.dialogShape.modifier
                    .then(animateModifier),
            ) {
                Row(
                    modifier = theme.modifier
                        .then(animateModifier),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    for ((name, resId) in mapping) {
                        ReactionDialogButton(
                            imagePainter = painterResource(id = resId),
                            onSelected = {
                                onSelected(name)
                                onClose()
                            },
                            modifier = Modifier
                                .weight(1f, true)
                                .aspectRatio(1f)
                                .clip(CircleShape),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MessageReactions(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (String) -> Unit,
        onAdd: () -> Unit,
    ) {

        val theme = LocalReactionTheme.current
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Get only defined emojis
            val filteredReactions = reactions.filter { it.reaction in mapping.keys }
            items(filteredReactions) { reaction ->
                val reactionTheme =
                    if (reaction.members.any { it.id == currentUserId }) theme.selectedReaction else theme.unselectedReaction
                ReactionButton(
                    imagePainter = painterResource(id = mapping[reaction.reaction]!!),
                    text = "${reaction.members.size}",
                    onSelected = { onSelected(reaction.reaction) },
                    theme = reactionTheme,
                )
            }

            if (reactions.size in (1 until mapping.size)) {
                item {
                    ReactionButton(
                        imagePainter = painterResource(id = R.drawable.ic_emoji_add),
                        text = "+",
                        onSelected = onAdd,
                        theme = theme.unselectedReaction,
                    )
                }
            }
        }
    }

    @Composable
    private fun ReactionDialogButton(
        imagePainter: Painter,
        onSelected: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Emoji",
            modifier = modifier
                .semantics {
                    contentDescription = "reaction"
                }
                .clickable(onClick = onSelected),
        )
    }

    @Composable
    private fun ReactionButton(
        imagePainter: Painter,
        text: String,
        theme: ButtonTheme,
        onSelected: () -> Unit,
    ) {
        Button(
            onClick = { onSelected() },
            elevation = theme.elevation,
            shape = theme.shape,
            border = theme.border,
            colors = theme.colors,
            contentPadding = theme.contentPadding,
            modifier = theme.modifier.semantics {
                contentDescription = "reaction"
            },
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Emoji",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
            )
            Spacer(Modifier.width(6.dp))
            Text(text = text, theme = theme.text)
            Spacer(Modifier.width(2.dp))
        }
    }


    @Composable
    private fun Text(text: String, theme: TextTheme, modifier: Modifier = theme.modifier) {
        androidx.compose.material.Text(
            text = text,
            fontWeight = theme.fontWeight,
            fontSize = theme.fontSize,
            color = theme.color,
            textAlign = theme.textAlign,
            overflow = theme.overflow,
            maxLines = theme.maxLines,
            modifier = modifier,//.border(1.dp, Color.random),
        )
    }
}

typealias EmojiMap = Map<String, Int>
