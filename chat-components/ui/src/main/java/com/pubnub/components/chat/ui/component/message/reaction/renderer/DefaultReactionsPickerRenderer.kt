package com.pubnub.components.chat.ui.component.message.reaction.renderer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.pubnub.components.chat.ui.component.message.reaction.*
import com.pubnub.components.chat.util.AutoSizeText
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.launch
import kotlin.math.floor

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
object DefaultReactionsPickerRenderer : ReactionsRenderer {

    private const val VISIBLE_ITEMS_COUNT = 6

    var emojis: List<Emoji> = listOf(
        UnicodeEmoji("\uD83D\uDC4D"),    // 👍 thumbs up
        UnicodeEmoji("\u2764"),          // ❤ red heart U+2764
        UnicodeEmoji("\uD83D\uDE02"),    // 😂 face with tears of joy U+1F602
        UnicodeEmoji("\uD83D\uDE32"),    // 😲 astonished face U+1F632
        UnicodeEmoji("\uD83D\uDE22"),    // 😢 crying face U+1F622
        UnicodeEmoji("\uD83D\uDD25"),    // 🔥 fire U+1F525
    )
    @Composable
    override fun Picker(
        onSelected: (Emoji) -> Unit,
    ) {
        ReactionsPicker(
            onSelected = onSelected,
        )
    }

    @Composable
    override fun PickedList(
        currentUserId: UserId,
        reactions: List<ReactionUi>,
        onSelected: (Emoji) -> Unit,
    ) {
        PickedReactions(
            currentUserId = currentUserId,
            reactions = reactions,
            onSelected = onSelected,
        )
    }

    // region ReactionsPicker
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ReactionsBottomSheetLayout(
        sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
        onSelected: (Emoji) -> Unit,
        content: @Composable () -> Unit
    ){
        val theme = LocalReactionTheme.current
        val coroutineScope = rememberCoroutineScope()
        val action: (Emoji) -> Unit = {
            onSelected(it)

            coroutineScope.launch { sheetState.hide() }
        }
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = { ReactionsPicker(onSelected = action) },
            sheetShape = theme.dialogShape.shape,
            sheetElevation = ModalBottomSheetDefaults.Elevation, // TODO: add elevation to theme
            sheetBackgroundColor = theme.dialogShape.tint, // TODO: modify theme
            sheetContentColor = contentColorFor(theme.dialogShape.tint), // TODO: modify theme
            scrimColor = ModalBottomSheetDefaults.scrimColor, // TODO: modify theme
        ) {
            content()
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun ReactionsPicker(
        onSelected: (Emoji) -> Unit,
    ) {
        val theme = LocalReactionTheme.current

        var itemWidth by remember { mutableStateOf(64.dp) }
        BoxWithConstraints {
            itemWidth = floor(this.maxWidth.value / VISIBLE_ITEMS_COUNT).dp
            Row(
                modifier = theme.modifier
                    .horizontalScroll(rememberScrollState()),
                //horizontalArrangement = Arrangement.spacedBy(12.dp), // TODO: modify theme
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
        when(emoji){
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
        ){
            AutoText(
                text = text,
                theme = theme.unselectedReaction.text,
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
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 6.dp,
            crossAxisSpacing = 6.dp,
        ) {
            // Get only defined emojis
            val filteredReactions = reactions.filter { reaction ->
                emojis.any { it.type == reaction.type && it.value == reaction.value }
            }

            for(reaction in filteredReactions) {
                val reactionTheme =
                    if (reaction.members.any { it.id == currentUserId }) theme.selectedReaction else theme.unselectedReaction

                val emoji = reaction.toEmoji()
                PickedReactionButton(
                    emoji = emoji,
                    text = "${reaction.members.size}",
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
            when(emoji){
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
        when(type){
            "reaction" -> UnicodeEmoji(value)
            else -> throw RuntimeException("Cannot map reaction [$type:$value] to Emoji object")
        }
}

@Preview(widthDp = 200)
@Preview(device = Devices.PIXEL_2_XL)
@Composable
private fun PickedUnicodeReactionsButton(){
    val member1 = MemberUi.Data("member1", "Member Nr 1")
    val member2 = MemberUi.Data("member2", "Member Nr 2")
    val member3 = MemberUi.Data("member3", "Member Nr 3")

    CompositionLocalProvider(LocalReactionTheme provides DefaultReactionTheme) {
        DefaultReactionsPickerRenderer.PickedList(
            "member1",
            listOf(
                ReactionUi("\u1F44D", "reaction", listOf(member1)),                         // 👍 thumbs up
                ReactionUi("\u2764", "reaction", listOf(member1, member2)),                 // ❤ red heart U+2764
                ReactionUi("\u1F602", "reaction", listOf(member1, member2, member3)),       // 😂 face with tears of joy U+1F602
                ReactionUi("\u1F632", "reaction", listOf(member2, member3)),                // 😲 astonished face U+1F632
                ReactionUi("\u1F622", "reaction", listOf(member2)),                         // 😢 crying face U+1F622
                ReactionUi("\u1F525", "reaction", listOf(member3)),                         // 🔥 fire U+1F525
            ),
        ) {}
    }
}

@Preview(widthDp = 200, heightDp = 600)
@Preview(device = Devices.PIXEL_2_XL, heightDp = 600)
@Composable
private fun ReactionsPicker(){
    CompositionLocalProvider(LocalReactionTheme provides DefaultReactionTheme) {
        DefaultReactionsPickerRenderer.ReactionsPicker{}
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(widthDp = 200, heightDp = 600)
@Preview(device = Devices.PIXEL_2_XL, heightDp = 600)
@Composable
private fun ReactionsPickerBottomSheet(){
    CompositionLocalProvider(LocalReactionTheme provides DefaultReactionTheme) {
        DefaultReactionsPickerRenderer.ReactionsBottomSheetLayout(
            sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
            onSelected = {},
        ) {
            Text("Hello")
        }
    }
}
