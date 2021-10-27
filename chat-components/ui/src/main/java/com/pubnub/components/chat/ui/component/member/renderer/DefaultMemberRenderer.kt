package com.pubnub.components.chat.ui.component.member.renderer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.google.accompanist.placeholder.placeholder
import com.pubnub.components.chat.ui.R
import com.pubnub.components.chat.ui.component.member.LocalMemberListTheme
import com.pubnub.components.chat.ui.component.member.ProfileImage

@OptIn(ExperimentalFoundationApi::class)
object DefaultMemberRenderer : MemberRenderer {
    @Composable
    override fun Member(
        name: String,
        description: String,
        profileUrl: String,
        online: Boolean?,
        onClick: () -> Unit,
        modifier: Modifier
    ) {
        MemberItemView(
            name = name,
            description = description,
            profileUrl = profileUrl,
            online = online,
            clickAction = onClick,
            modifier = modifier,
        )
    }

    override fun renderSeparator(scope: LazyListScope, title: String?) {
        if (title != null) {
            scope.stickyHeader {
                MemberItemHeaderView(title)
            }
        }
    }

    @Composable
    override fun Separator(title: String) {
        MemberItemHeaderView(title)
    }

    @Composable
    override fun Placeholder() {
        MemberItemPlaceholderView()
    }

    @Composable
    fun MemberItemView(
        name: String,
        description: String,
        profileUrl: String,
        clickAction: () -> Unit,
        online: Boolean? = null,
        placeholder: Boolean = false,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current
        val theme = LocalMemberListTheme.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.clickable { clickAction() },
        ) {
            // icon
            val imagePlaceholder = Modifier.placeholder(
                visible = placeholder,
                color = Color.LightGray,
                shape = CircleShape,
            )

            Box(modifier = theme.image) {
                ProfileImage(
                    modifier = imagePlaceholder.then(
                        Modifier
                            .clickable(onClick = clickAction)
                            .semantics {
                                contentDescription = context.getString(
                                    R.string.profile_image
                                )
                            }),
                    imageUrl = profileUrl,
                    isOnline = online,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                // title
                Text(
                    text = name,
                    fontWeight = theme.name.fontWeight,
                    fontSize = theme.name.fontSize,
                    color = theme.name.color,
                    overflow = theme.name.overflow,
                    maxLines = theme.name.maxLines,
                    modifier = theme.name.modifier.placeholder(
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
        }
    }

    @Composable
    fun MemberItemPlaceholderView(
        modifier: Modifier = Modifier,
    ) {
        MemberItemView(
            name = "",
            description = "",
            profileUrl = "",
            clickAction = {},
            placeholder = true,
            modifier = modifier
        )
    }

    @Composable
    fun OnlineIndicator(
        isOnline: Boolean,
        activeColor: Color,
        inactiveColor: Color,
        borderStroke: BorderStroke,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        val color: Color = if (isOnline) activeColor else inactiveColor
        Box(
            modifier
                .semantics {
                    contentDescription =
                        context.getString(if (isOnline) R.string.online_presence_indicator_member_list else R.string.offline_presence_indicator_member_list)
                }
                .background(color, CircleShape)
                .border(borderStroke.width, borderStroke.brush, CircleShape),
        ) {
            if (!isOnline)
                Box(
                    Modifier
                        .fillMaxSize(0.4f)
                        .background(borderStroke.brush, CircleShape)
                        .align(Alignment.Center)
                )
        }
    }

    @Composable
    fun MemberItemHeaderView(
        title: String,
    ) {
        val theme = LocalMemberListTheme.current

        Text(
            text = title,
            fontWeight = theme.header.fontWeight,
            fontSize = theme.header.fontSize,
            color = theme.header.color,
            overflow = theme.header.overflow,
            maxLines = theme.header.maxLines,
            modifier = theme.header.modifier,
        )
    }

}