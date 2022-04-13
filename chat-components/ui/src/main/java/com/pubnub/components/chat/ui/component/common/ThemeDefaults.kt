package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pubnub.components.chat.ui.component.channel.ChannelListTheme
import com.pubnub.components.chat.ui.component.input.MessageInputTheme
import com.pubnub.components.chat.ui.component.input.TypingIndicatorTheme
import com.pubnub.components.chat.ui.component.member.MemberListTheme
import com.pubnub.components.chat.ui.component.menu.MenuItemTheme
import com.pubnub.components.chat.ui.component.message.IndicatorTheme
import com.pubnub.components.chat.ui.component.message.MessageListTheme
import com.pubnub.components.chat.ui.component.message.MessageTheme
import com.pubnub.components.chat.ui.component.message.ProfileImageTheme
import com.pubnub.components.chat.ui.component.message.reaction.ReactionTheme

object ThemeDefaults {

    @Composable
    fun messageInput(
        modifier: Modifier = Modifier
            .fillMaxWidth(),
        input: InputTheme = InputThemeDefaults.input(),
        button: ButtonTheme = ButtonThemeDefaults.button(),
    ) = MessageInputTheme(modifier, input, button)

    @Composable
    fun typingIndicator(
        modifier: Modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        icon: IconTheme = IconThemeDefaults.icon(
            tint = MaterialTheme.colors.primaryVariant.copy(
                alpha = ContentAlpha.medium
            )
        ),
        text: TextTheme = TextThemeDefaults.text(
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f)
        ),
    ) = TypingIndicatorTheme(modifier, icon, text)

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun reaction(
        pickerModifier: Modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        listFlowRow: FlowRowTheme = FlowRowThemeDefaults.reactionList(),
        dialog: ModalBottomSheetLayoutTheme = ModalBottomSheetLayoutThemeDefaults.reaction(),
        selectedReaction: ButtonTheme = ButtonThemeDefaults.selectedReaction(),
        notSelectedReaction: ButtonTheme = ButtonThemeDefaults.notSelectedReaction(),
    ) = ReactionTheme(
        pickerModifier,
        listFlowRow,
        selectedReaction,
        notSelectedReaction,
        dialog,
    )

    @Composable
    fun channelList(
        modifier: Modifier = Modifier.fillMaxWidth(),
        title: TextTheme = TextThemeDefaults.title(),
        description: TextTheme = TextThemeDefaults.subtitle(),
        image: Modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .size(36.dp),
        icon: IconTheme = IconThemeDefaults.icon(Icons.Default.Logout),
        header: TextTheme = TextThemeDefaults.header(),
    ) = ChannelListTheme(modifier, title, description, image, icon, header)

    @Composable
    fun memberList(
        modifier: Modifier = Modifier.fillMaxWidth(),
        name: TextTheme = TextThemeDefaults.title(),
        description: TextTheme = TextThemeDefaults.subtitle(),
        image: Modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .size(36.dp),
        icon: IconTheme = IconThemeDefaults.icon(Icons.Default.Logout),
        header: TextTheme = TextThemeDefaults.header(),
    ) = MemberListTheme(modifier, name, description, image, icon, header)

    // region Message
    @Composable
    fun messageList(
        modifier: Modifier = Modifier.fillMaxWidth(),
        arrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
        message: MessageTheme = message(),
        messageOwn: MessageTheme = message(
            title = TextThemeDefaults.messageTitle(MaterialTheme.colors.primary),
            shape = ShapeThemeDefaults.messageBackground(
                color = MaterialTheme.colors.primary.copy(
                    alpha = 0.4f
                )
            )
        ),
        separator: TextTheme = TextThemeDefaults.messageSeparator(),
    ) = MessageListTheme(modifier, arrangement, message, messageOwn, separator)

    @Composable
    fun message(
        modifier: Modifier = Modifier
            .padding(16.dp, 12.dp)
            .fillMaxWidth(),
        title: TextTheme = TextThemeDefaults.messageTitle(),
        date: TextTheme = TextThemeDefaults.messageDate(),
        text: TextTheme = TextThemeDefaults.messageText(),
        profileImage: ProfileImageTheme = profileImage(),
        shape: ShapeTheme = ShapeThemeDefaults.messageBackground(),
        previewShape: ShapeTheme = ShapeThemeDefaults.linkPreview(),
        previewImageShape: ShapeTheme = ShapeThemeDefaults.linkPreviewImage(),
        verticalAlignment: Alignment.Vertical = Alignment.Top,
    ) = MessageTheme(
        modifier,
        title,
        date,
        text,
        profileImage,
        shape,
        previewShape,
        previewImageShape,
        verticalAlignment
    )

    @Composable
    fun profileImage(
        modifier: Modifier = Modifier
            .padding(0.dp, 0.dp, 12.dp, 0.dp)
            .size(32.dp),
        indicator: IndicatorTheme = indicator(),
    ) = ProfileImageTheme(modifier, indicator)

    @Composable
    fun indicator(
        modifier: Modifier = Modifier.size(16.dp),
        alignment: Alignment = Alignment.BottomEnd,
        activeColor: Color = Color(0xFFb8e986),
        inactiveColor: Color = Color(0xFF9b9b9b),
        borderStroke: BorderStroke = BorderStroke(2.dp, Color.White),
    ) = IndicatorTheme(modifier, alignment, activeColor, inactiveColor, borderStroke)
    // endregion

    // region Menu
    @Composable
    fun menuItem(
        modifier: Modifier = Modifier.fillMaxWidth(),
        text: TextTheme = TextThemeDefaults.menuItemTitle(),
        icon: IconTheme = IconThemeDefaults.menuIcon(),
        horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
        verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    ) = MenuItemTheme(modifier, text, icon, horizontalArrangement, verticalAlignment)
    // endregion
}
