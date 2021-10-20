package com.pubnub.components.chat.ui.component.message.renderer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
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
import com.pubnub.framework.data.MessageId
import com.pubnub.framework.data.UserId
import com.pubnub.framework.util.Timetoken
import com.pubnub.framework.util.seconds
import kotlinx.coroutines.*
import timber.log.Timber
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
        attachments: List<Attachment>?,
        timetoken: Timetoken,
        navigateToProfile: (UserId) -> Unit,
    ) {
        GroupChatMessage(
            currentUserId = currentUserId,
            userId = userId,
            profileUrl = profileUrl,
            online = online,
            title = title,
            message = message,
            attachments = attachments,
            timetoken = timetoken,
            navigateToProfile = navigateToProfile,
        )
    }

    @Composable
    override fun Placeholder() {
        GroupChatMessagePlaceholder()
    }

    @Composable
    override fun Separator(text: String) {
        val separatorTheme = LocalMessageListTheme.current.separator
        val messageTheme = LocalMessageListTheme.current.message.text
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.disabled,
        ) {
            Row(
                modifier = separatorTheme.modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Divider(modifier = Modifier.weight(1f), color = messageTheme.color)
                Spacer(modifier = Modifier.width(8.dp))

                Text(text = text, theme = messageTheme)

                Spacer(modifier = Modifier.width(8.dp))
                Divider(modifier = Modifier.weight(1f), color = messageTheme.color)
            }
        }
    }

    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private fun Timetoken.formatDate() =
        dateFormat.format(this.seconds).uppercase()

    @Composable
    fun GroupChatMessage(
        currentUserId: UserId,
        userId: UserId,
        profileUrl: String?,
        online: Boolean?,
        title: String,
        message: AnnotatedString?,
        attachments: List<Attachment>?,
        timetoken: Timetoken,
        navigateToProfile: (UserId) -> Unit,
        placeholder: Boolean = false,
    ) {
        val context = LocalContext.current
        val theme = LocalMessageListTheme.current.message

        val date = timetoken.formatDate()

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
            modifier = theme.modifier,
            verticalAlignment = theme.verticalAlignment,
        ) {
            Box(modifier = theme.profileImage.modifier) {
                ProfileImage(
                    modifier = imagePlaceholder.then(
                        Modifier
                            .clickable(onClick = { navigateToProfile(userId) })
                            .semantics {
                                contentDescription = context.getString(
                                    R.string.profile_image
                                )
                            }),
                    imageUrl = profileUrl,
                    isOnline = online,//!isOwn && isOnline,
                )
            }

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = title, theme = theme.title, modifier = theme.title.modifier.then(
                            titlePlaceholder
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
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
                            attachments?.images?.forEach { image ->
                                ChatImage(image.imageUrl)
                            }
                            attachments?.links?.forEach { link ->
                                LinkPreview(
                                    link.link,
                                    theme.text,
                                    theme.date,
                                    theme.previewShape,
                                    theme.previewImageShape
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
            attachments = null,
            timetoken = 0L,
            placeholder = true,
            navigateToProfile = {},
        )
    }

    @Composable
    private fun Text(text: String, theme: TextTheme, modifier: Modifier = theme.modifier) {
        Text(
            text = text,
            fontWeight = theme.fontWeight,
            fontSize = theme.fontSize,
            color = theme.color,
            overflow = theme.overflow,
            maxLines = theme.maxLines,
            modifier = modifier,//.border(1.dp, Color.random),
        )
    }

    val Color.Companion.random
        get() = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))

    @Composable
    fun ChatText(
        message: AnnotatedString,
        theme: TextTheme,
//        navigateToProfile: (UserId) -> Unit = { Timber.w("Navigate to profile $it not implemented") },
        placeholder: Modifier = Modifier,
        modifier: Modifier = Modifier,
    ) {

//        BasicText(
//            text = message,
//            modifier = modifier.then(placeholder),
//            style = theme.asStyle(),
//        )

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
                        Timber.e("Annotation $annotation")
                        when (annotation.tag) {
                            SymbolAnnotationType.LINK.name -> {
                                uriHandler.openUri(annotation.item)
                            }
//                            SymbolAnnotationType.PERSON.name -> navigateToProfile(annotation.item)
                            else -> Unit
                        }
                    }
            }
        )
    }

    @Composable
    fun LinkPreview(
        link: String,
        titleTheme: TextTheme,
        descriptionTheme: TextTheme,
        shape: ShapeTheme,
        imageShape: ShapeTheme,
        coroutineScope: CoroutineScope = GlobalScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        // TODO: 5/25/21 this part should be moved - metadata should be stored in db!
        var content by remember { mutableStateOf<LinkPreview.Content?>(null) }

        DisposableEffect(key1 = link) {
            val job = coroutineScope.async(dispatcher) {
                content = LinkPreview.getContent(link)
            }
            onDispose { job.cancel() }
        }

        if (content != null) {
            Timber.e("Content: ${content}")
            LinkPreview(
                url = link,
                imageUrl = content!!.imageUrl,
                title = content!!.title ?: link,
                description = content!!.description,
                titleTheme = titleTheme,
                descriptionTheme = descriptionTheme,
                shape = shape,
                imageShape = imageShape,
            )
        }
    }

    @Composable
    private fun LinkPreview(
        url: String,
        imageUrl: String?,
        title: String?,
        description: String?,
        titleTheme: TextTheme,
        descriptionTheme: TextTheme,
        shape: ShapeTheme,
        imageShape: ShapeTheme,
    ) {
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(1.dp)
                .combinedClickable(onClick = { uriHandler.openUri(url) })
//                        .pointerInput(Unit) {
//                            detectTapGestures(
//                                onTap = { uriHandler.openUri(url) },
//                            )
//                        }
        ) {
            if (!imageUrl.isNullOrBlank())
                ChatImage(
                    imageUrl = imageUrl,
                    modifier = imageShape.modifier
                        .clip(imageShape.shape)
                        .padding(imageShape.padding)
                )
            if (!title.isNullOrBlank()) {
                Column(
                    shape.modifier
                        .fillMaxWidth()
                        .background(color = shape.tint, shape.shape)
                        .padding(shape.padding)
                ) {
                    Text(text = title, theme = titleTheme)
                    if (!description.isNullOrBlank()) Text(
                        text = description,
                        theme = descriptionTheme
                    )
                }
            }
        }
    }

    @Composable
    fun ChatImage(
        imageUrl: String,
        modifier: Modifier = Modifier.defaultMinSize(200.dp, 200.dp)
    ) {
        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                crossfade(true)
            }
        )

        Image(
            painter = painter,
            contentDescription = imageUrl,
            alignment = Alignment.TopStart,
            modifier = modifier,
        )
    }
}
