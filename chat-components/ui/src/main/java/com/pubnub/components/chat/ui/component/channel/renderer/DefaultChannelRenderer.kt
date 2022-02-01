package com.pubnub.components.chat.ui.component.channel.renderer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.placeholder.placeholder
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.channel.LocalChannelListTheme
import com.pubnub.components.chat.util.CenterInside
import org.jetbrains.annotations.Async

@OptIn(ExperimentalFoundationApi::class, ExperimentalCoilApi::class)
object DefaultChannelRenderer : ChannelRenderer {
    @Composable
    override fun Channel(
        name: String,
        description: String,
        modifier: Modifier,
        profileUrl: String?,
        onClick: (() -> Unit)?,
        onLeave: (() -> Unit)?,
    ) {
        ChannelItemView(
            title = name,
            description = description,
            iconUrl = profileUrl,
            clickAction = onClick,
            leaveAction = onLeave,
            modifier = modifier,
        )
    }

    @Composable
    override fun Placeholder() {
        ChannelItemPlaceholderView()
    }

    @Composable
    override fun Separator(title: String?, onClick: (() -> Unit)?) {
        if (title != null) {
            ChannelItemHeaderView(title, onClick)
        }
    }

    override fun renderSeparator(scope: LazyListScope, title: String?, onClick: (() -> Unit)?) {
        if (title != null) {
            scope.stickyHeader(title) {
                ChannelItemHeaderView(title, onClick)
            }
        }
    }

    @Composable
    fun ChannelItemView(
        title: String,
        description: String,
        modifier: Modifier = Modifier,
        iconUrl: String? = null,
        clickAction: (() -> Unit)? = null,
        leaveAction: (() -> Unit)? = null,
        placeholder: Boolean = false,
    ) {

        val theme = LocalChannelListTheme.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.apply{ clickAction?.let { action -> clickable { action() } } },
        ) {
            // icon
            Image(
                painter = rememberImagePainter(
                    data = iconUrl,
                    builder = {
                        crossfade(false)
                        placeholder(drawableResId = R.drawable.ic_baseline_account_circle_24)
                        transformations(CircleCropTransformation())
                    }
                ),
                contentDescription = LocalContext.current.resources.getString(R.string.thumbnail),
                contentScale = CenterInside,
                modifier = theme.image.placeholder(visible = placeholder, color = Color.Gray),
            )

            Column(modifier = Modifier.weight(1f)) {
                // title
                Text(
                    text = title,
                    fontWeight = theme.title.fontWeight,
                    fontSize = theme.title.fontSize,
                    color = theme.title.color,
                    overflow = theme.title.overflow,
                    maxLines = theme.title.maxLines,
                    modifier = theme.title.modifier.placeholder(
                        visible = placeholder,
                        color = Color.Gray
                    ),
                )

                // description
                Text(
                    text = description,
                    fontWeight = theme.description.fontWeight,
                    fontSize = theme.description.fontSize,
                    color = theme.description.color,
                    overflow = theme.description.overflow,
                    maxLines = theme.description.maxLines,
                    modifier = theme.description.modifier.placeholder(
                        visible = placeholder,
                        color = Color.Gray
                    ),
                )
            }

            // leave icon
            if (leaveAction != null && theme.icon.icon != null)
                Icon(
                    imageVector = theme.icon.icon!!,
                    contentDescription = LocalContext.current.resources.getString(R.string.leave),
                    modifier = Modifier
                        .clip(theme.icon.shape)
                        .clickable { leaveAction() }
                        .then(theme.icon.modifier),
                    tint = theme.icon.tint,
                )
        }
    }

    @Composable
    fun ChannelItemHeaderView(
        title: String,
        onClick: (() -> Unit)? = null,
        placeholder: Boolean = false,
    ) {
        val theme = LocalChannelListTheme.current

        Row(modifier = theme.header.modifier, horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                text = title.uppercase(),
                fontWeight = theme.header.fontWeight,
                fontSize = theme.header.fontSize,
                color = theme.header.color,
                overflow = theme.header.overflow,
                maxLines = theme.header.maxLines,
                modifier = Modifier.placeholder(
                    visible = placeholder,
                    color = Color.Gray
                ),
            )

            onClick?.let { action ->
                Icon(
                    imageVector = Icons.Rounded.AddCircleOutline,
                    contentDescription = LocalContext.current.resources.getString(R.string.add),
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable(onClick = action),
                    tint = MaterialTheme.colors.onPrimary,
                )
            }

        }
    }

    @Composable
    fun ChannelItemPlaceholderView(
        modifier: Modifier = Modifier,
    ) {
        ChannelItemView(
            title = "Test Channel",
            description = "Some long channel description",
            iconUrl = "",
            clickAction = {},
            placeholder = true,
            modifier = modifier
        )
    }
}