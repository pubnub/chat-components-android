package com.pubnub.components.chat.ui.component.message.renderer

import android.util.Patterns.EMAIL_ADDRESS
import android.webkit.URLUtil
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.placeholder
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.common.ShapeTheme
import com.pubnub.components.chat.ui.component.common.TextTheme
import com.pubnub.components.chat.ui.component.member.ProfileImage
import com.pubnub.components.chat.ui.component.message.*
import com.pubnub.components.chat.ui.component.message.reaction.PickedReaction
import com.pubnub.components.chat.ui.component.message.reaction.Reaction
import com.pubnub.components.chat.ui.component.message.reaction.ReactionUi
import com.pubnub.components.chat.ui.component.message.reaction.renderer.DefaultReactionsPickerRenderer
import com.pubnub.components.chat.ui.component.message.reaction.renderer.ReactionsRenderer
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@OptIn(
    ExperimentalFoundationApi::class,
    DelicateCoroutinesApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalCoilApi::class,
)

object GroupMessageRenderer : MessageRenderer {

    @Composable
    override fun Message(
        messageId: MessageId,
        currentUserId: UserId,
        userId: UserId,
        profileUrl: String,
        online: Boolean?,
        title: String,
        message: AnnotatedString?,
        timetoken: Timetoken,
        reactions: List<ReactionUi>,
        onMessageSelected: (() -> Unit)?,
        onReactionSelected: ((PickedReaction) -> Unit)?,
        reactionsPickerRenderer: ReactionsRenderer,
    ) {
        val onReaction: ((Reaction) -> Unit)? = onReactionSelected?.let {
            { reaction ->
                onReactionSelected(
                    PickedReaction(
                        currentUserId,
                        timetoken,
                        reaction.type,
                        reaction.value
                    )
                )
            }
        }
        GroupChatMessage(
            currentUserId = currentUserId,
            userId = userId,
            profileUrl = profileUrl,
            online = online,
            title = title,
            message = message,
            timetoken = timetoken,
            reactions = reactions,
            onMessageSelected = onMessageSelected?.let { { it() } },
            onReactionSelected = onReaction,
            reactionsPicker = reactionsPickerRenderer,
        )
    }

    @Composable
    override fun Placeholder() {
        GroupChatMessagePlaceholder()
    }

    @Composable
    override fun Separator(text: String) {
//        val separatorTheme = LocalMessageListTheme.current.separator
//        val messageTheme = LocalMessageListTheme.current.message.text
//        CompositionLocalProvider(
//            LocalContentAlpha provides ContentAlpha.disabled,
//        ) {
//            Row(
//                modifier = separatorTheme.modifier,
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Divider(modifier = Modifier.weight(1f), color = messageTheme.color)
//                Spacer(modifier = Modifier.width(8.dp))
//
//                Text(text = text, theme = messageTheme)
//
//                Spacer(modifier = Modifier.width(8.dp))
//                Divider(modifier = Modifier.weight(1f), color = messageTheme.color)
//            }
//        }
    }

    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private fun Timetoken.formatDate() =
        dateFormat.format(this.seconds).uppercase()

    @Composable
    fun GroupChatMessage(
        @Suppress("UNUSED_PARAMETER") currentUserId: UserId,
        @Suppress("UNUSED_PARAMETER") userId: UserId,
        profileUrl: String?,
        online: Boolean?,
        title: String,
        message: AnnotatedString?,
        timetoken: Timetoken,
        placeholder: Boolean = false,
        reactions: List<ReactionUi> = emptyList(),
        onMessageSelected: (() -> Unit)? = null,
        onReactionSelected: ((Reaction) -> Unit)? = null,
        reactionsPicker: ReactionsRenderer = DefaultReactionsPickerRenderer,
    ) {
        val theme = LocalMessageListTheme.current.message

        val date = timetoken.formatDate()
        val reactionEnabled = onReactionSelected != null

        // region Placeholders
        val messagePlaceholder = Modifier.placeholder(
            visible = placeholder,
            color = Color.LightGray,
            shape = theme.shape.shape,
        ).let { if (placeholder) it.fillMaxWidth(0.8f) else it }

        val imagePlaceholder = Modifier.placeholder(
            visible = placeholder,
            color = Color.LightGray,
            shape = CircleShape,
        )

        val titlePlaceholder =
            Modifier
                .let {
                    if (placeholder) it
                        .padding(bottom = 4.dp)
                        .fillMaxWidth(0.4f) else it
                }
                .placeholder(
                    visible = placeholder,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp),
                )

        val datePlaceholder =
            Modifier
                .let {
                    if (placeholder) it
                        .padding(bottom = 4.dp)
                        .fillMaxWidth(0.2f) else it
                }
                .placeholder(
                    visible = placeholder,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp),
                )
        // endregion

        Row(
            modifier = Modifier.combinedClickable(
                enabled = onMessageSelected != null,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onLongClick = { onMessageSelected?.let { onMessageSelected() } },
                onClick = { },
            ).then(theme.modifier),
            verticalAlignment = theme.verticalAlignment,
        ) {
            Box(modifier = theme.profileImage.modifier) {
                ProfileImage(
                    modifier = imagePlaceholder,
                    imageUrl = profileUrl,
                    isOnline = online,
                )
            }

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    ThemedText(
                        text = title, theme = theme.title, modifier = theme.title.modifier.then(
                            titlePlaceholder
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ThemedText(
                        text = date, theme = theme.date, modifier = theme.date.modifier.then(
                            datePlaceholder
                        )
                    )
                }

                val body: @Composable() () -> Unit = {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Column {
                            if (message != null && message.isNotBlank()) {
                                ChatText(
                                    message,
                                    theme.text,
//                                    navigateToProfile,
                                    theme.text.modifier.then(
                                        messagePlaceholder.then(
                                            Modifier.padding(
                                                theme.shape.padding
                                            )
                                        )
                                    ),
                                )
                            }
                        }
                    }
                }

                // workaround for placeholders...
                if (placeholder) body()
                else Surface(
                    color = theme.shape.tint,
                    shape = theme.shape.shape,
                    modifier = theme.shape.modifier
                ) { body() }

                if (reactionEnabled && reactions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    reactionsPicker.PickedList(
                        currentUserId,
                        reactions,
                        onReactionSelected!!
                    )
                }
            }
        }
    }

    @Composable
    fun GroupChatMessagePlaceholder(
    ) {
        GroupChatMessage(
            currentUserId = "a",
            userId = "b",
            profileUrl = "",
            online = false,
            title = "Lorem ipsum dolor",
            message = messageFormatter(text = "Test message"),
            timetoken = 0L,
            placeholder = true,
            reactionsPicker = DefaultReactionsPickerRenderer,
        )
    }

    @Composable
    fun ThemedText(text: String, theme: TextTheme, modifier: Modifier = theme.modifier) {
        Text(
            text = text,
            fontWeight = theme.fontWeight,
            fontSize = theme.fontSize,
            color = theme.color,
            overflow = theme.overflow,
            maxLines = theme.maxLines,
            modifier = modifier,
        )
    }

    val Color.Companion.random
        get() = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

    @Composable
    fun ChatText(
        message: AnnotatedString,
        theme: TextTheme,
        placeholder: Modifier = Modifier,
        modifier: Modifier = Modifier,
    ) {

        val uriHandler = LocalUriHandler.current
        ClickableText(
            text = message,
            modifier = modifier.then(placeholder),
            style = theme.asStyle(),
            onClick = {
                message
                    .getStringAnnotations(start = it, end = it)
                    .firstOrNull()
                    ?.let { annotation ->
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> {
                                val url = when {
                                    annotation.item.startsWith("mailto:") -> annotation.item
                                    EMAIL_ADDRESS.matcher(annotation.item).matches() -> "mailto:${annotation.item}"
                                    else -> URLUtil.guessUrl(annotation.item)
                                }

                                try {
                                    uriHandler.openUri(url)
                                } catch (e: Exception) {
                                    // todo: add error handler
                                }
                            }
                            else -> Unit
                        }
                    }
            }
        )
    }
}
