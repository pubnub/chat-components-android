package com.pubnub.components.chat.ui.component.message.reaction.renderer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.pubnub.components.chat.ui.component.common.ButtonTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.menu.MenuDefaults
import com.pubnub.components.chat.ui.component.message.reaction.*
import com.pubnub.components.chat.util.AutoSizeText
import com.pubnub.framework.data.UserId
import kotlin.math.floor

object DefaultReactionsPickerRenderer : ReactionsRenderer {

    var visibleItemsCount: Int = 6

    var emojis: List<Emoji> = MenuDefaults.reactions()

    @Composable
    override fun Picker(
        onSelected: (Reaction) -> Unit,
    ) {
        ReactionsPicker(
            onSelected = onSelected,
        )
    }

    @Composable
    override fun PickedList(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (Reaction) -> Unit,
    ) {
        PickedReactions(
            currentUserId = currentUserId,
            reactions = reactions,
            onSelected = onSelected,
        )
    }

    @Composable
    fun ReactionsPicker(
        onSelected: (Reaction) -> Unit,
    ) {
        val theme = LocalReactionTheme.current

        var itemWidth by remember { mutableStateOf(64.dp) }
        BoxWithConstraints {
            itemWidth = floor(this.maxWidth.value / visibleItemsCount).dp
            Row(
                modifier = theme.pickerModifier,
            ) {
                val modifier = Modifier.size(itemWidth)

                for (emoji in emojis) {
                    ReactionsPickerButton(
                        emoji = emoji,
                        onSelected = { onSelected(emoji) },
                        modifier = modifier
                    )
                }
            }
        }
    }

    @Composable
    private fun ReactionsPickerButton(
        emoji: Emoji,
        onSelected: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        when (emoji) {
            is UnicodeEmoji -> {
                ReactionsPickerTextButton(
                    text = emoji.value,
                    onSelected = { onSelected() },
                    modifier = modifier,
                )
            }
            is LocalResourceEmoji -> {
                ReactionsPickerImageButton(
                    imagePainter = painterResource(id = emoji.drawable),
                    onSelected = { onSelected() },
                    modifier = modifier,
                )
            }
        }
    }

    @Composable
    private fun ReactionsPickerImageButton(
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
    private fun ReactionsPickerTextButton(
        text: String,
        onSelected: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val theme = LocalReactionTheme.current

        TextButton(
            onClick = onSelected,
            modifier = modifier
                .semantics {
                    contentDescription = "reaction"
                }
                .clickable(onClick = onSelected),
        ) {
            AutoText(
                text = text,
                theme = theme.notSelectedReaction.text,
            )
        }
    }
    // endregion

    // region PickedReactions
    @Composable
    private fun PickedReactions(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (Emoji) -> Unit,
    ) {
        val theme = LocalReactionTheme.current

        FlowRow(
            modifier = theme.listFlowRow.modifier,
            mainAxisSize = theme.listFlowRow.mainAxisSize,
            mainAxisAlignment = theme.listFlowRow.mainAxisAlignment,
            mainAxisSpacing = theme.listFlowRow.mainAxisSpacing,
            crossAxisAlignment = theme.listFlowRow.crossAxisAlignment,
            crossAxisSpacing = theme.listFlowRow.crossAxisSpacing,
            lastLineMainAxisAlignment = theme.listFlowRow.lastLineMainAxisAlignment,
        ) {
            // Get only defined emojis
            val filteredReactions = reactions.filter { reaction ->
                emojis.any { it.type == reaction.type && it.value == reaction.value }
            }

            for (reaction in filteredReactions) {
                val reactionTheme =
                    if (reaction.members.any { it.id == currentUserId }) theme.selectedReaction else theme.notSelectedReaction

                val emoji = reaction.toEmoji()
                PickedReactionButton(
                    emoji = emoji,
                    text = if (reaction.members.size < 100) "${reaction.members.size}" else "99+",
                    onSelected = { onSelected(emoji) },
                    theme = reactionTheme,
                )
            }
        }
    }

    @Composable
    private fun PickedReactionButton(
        emoji: Emoji,
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
            when (emoji) {
                is UnicodeEmoji ->
                    Text(
                        text = emoji.value,
                        theme = theme.text,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                    )
                is LocalResourceEmoji ->
                    Image(
                        painter = painterResource(id = emoji.drawable),
                        contentDescription = "Emoji",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                    )
            }
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

    @Composable
    private fun AutoText(text: String, theme: TextTheme, modifier: Modifier = theme.modifier) {
        AutoSizeText(
            text = text,
            fontWeight = theme.fontWeight,
            color = theme.color,
            textAlign = theme.textAlign,
            overflow = theme.overflow,
            maxLines = theme.maxLines,
            modifier = modifier,//.border(1.dp, Color.random),
        )
    }
    // endregion

    private fun ReactionUi.toEmoji(): Emoji =
        when (type) {
            "reaction" -> UnicodeEmoji(value)
            else -> throw RuntimeException("Cannot map reaction [$type:$value] to Emoji object")
        }
}


@Preview
@Composable
private fun PickedUnicodeReactionsCount() {
    CompositionLocalProvider(LocalReactionTheme provides DefaultReactionTheme) {
        DefaultReactionsPickerRenderer.PickedList(
            "member1",
            listOf(
                ReactionUi(
                    "\uD83D\uDC4D",
                    "reaction",
                    List(99) { MemberUi.Data("member", "Member") }
                ),                         // 👍 thumbs up
                ReactionUi(
                    "\u2764",
                    "reaction",
                    List(100) { MemberUi.Data("member", "Member") }
                ),                      // ❤  red heart U+2764
            ),
        ) {}
    }
}

@Preview(widthDp = 200)
@Preview(device = Devices.PIXEL_2_XL)
@Composable
private fun PickedUnicodeReactionsButton() {
    val member1 = MemberUi.Data("member1", "Member Nr 1")
    val member2 = MemberUi.Data("member2", "Member Nr 2")
    val member3 = MemberUi.Data("member3", "Member Nr 3")

    CompositionLocalProvider(LocalReactionTheme provides DefaultReactionTheme) {
        DefaultReactionsPickerRenderer.PickedList(
            "member1",
            listOf(
                ReactionUi(
                    "\uD83D\uDC4D",
                    "reaction",
                    listOf(member1)
                ),                         // 👍 thumbs up
                ReactionUi(
                    "\u2764",
                    "reaction",
                    listOf(member1, member2)
                ),                      // ❤  red heart U+2764
                ReactionUi(
                    "\uD83D\uDE02",
                    "reaction",
                    listOf(member1, member2, member3)
                ),       // 😂 face with tears of joy U+1F602
                ReactionUi(
                    "\uD83D\uDE32",
                    "reaction",
                    listOf(member2, member3)
                ),                // 😲 astonished face U+1F632
                ReactionUi(
                    "\uD83D\uDE22",
                    "reaction",
                    listOf(member2)
                ),                         // 😢 crying face U+1F622
                ReactionUi(
                    "\uD83D\uDD25",
                    "reaction",
                    listOf(member3)
                ),                         // 🔥 fire U+1F525
            ),
        ) {}
    }
}

@Preview(widthDp = 200, heightDp = 600)
@Preview(device = Devices.PIXEL_2_XL, heightDp = 600)
@Composable
private fun ReactionsPicker() {
    CompositionLocalProvider(LocalReactionTheme provides DefaultReactionTheme) {
        DefaultReactionsPickerRenderer.ReactionsPicker {}
    }
}
