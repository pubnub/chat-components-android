package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pubnub.components.chat.ui.component.channel.ChannelListTheme
import com.pubnub.components.chat.ui.component.input.MessageInputTheme
import com.pubnub.components.chat.ui.component.input.TypingIndicatorTheme
import com.pubnub.components.chat.ui.component.member.MemberListTheme
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
        input: InputTheme = input(),
        button: ButtonTheme = button(),
    ) = MessageInputTheme(modifier, input, button)

    @Composable
    fun typingIndicator(
        modifier: Modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        icon: IconTheme = icon(tint = MaterialTheme.colors.primaryVariant.copy(alpha = ContentAlpha.medium)),
        text: TextTheme = text(
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f)
        ),
    ) = TypingIndicatorTheme(modifier, icon, text)

    @Composable
    fun reaction(
        modifier: Modifier = Modifier
            .fillMaxWidth(),
        selectedReaction: ButtonTheme = button(
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f),
                contentColor = Color(0xDE3F3F3F)
            ),
            border = BorderStroke(1.dp, Color(0xFFced6e0)),
            elevation = null,
            text = text(
                color = Color(0xDE3f3f3f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            ),
            contentPadding = PaddingValues(6.dp),
            modifier = Modifier
                .height(28.dp)
                .defaultMinSize(minWidth = 40.dp),
        ),
        unselectedReaction: ButtonTheme = button(
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFe9eef4),
                contentColor = Color(0xDE3F3F3F)
            ),
            border = BorderStroke(1.dp, Color(0xFFced6e0)),
            elevation = null,
            text = text(
                color = Color(0xDE3f3f3f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
            ),
            contentPadding = PaddingValues(6.dp),
            modifier = Modifier
                .height(28.dp)
                .defaultMinSize(minWidth = 40.dp),
        ),
        dialogShape: ShapeTheme = shape(
            modifier = Modifier
                .fillMaxWidth().padding(8.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colors.background,
        ),
    ) = ReactionTheme(
        modifier,
        selectedReaction,
        unselectedReaction,
        dialogShape,
    )

    @Composable
    fun channelList(
        modifier: Modifier = Modifier.fillMaxWidth(),
        title: TextTheme = TextThemeDefaults.title(),
        description: TextTheme = TextThemeDefaults.subtitle(),
        image: Modifier = Modifier
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .size(36.dp),
        icon: IconTheme = icon(Icons.Default.Logout),
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
        icon: IconTheme = icon(Icons.Default.Logout),
        header: TextTheme = TextThemeDefaults.header(),
    ) = MemberListTheme(modifier, name, description, image, icon, header)

    // region Message
    @Composable
    fun messageList(
        modifier: Modifier = Modifier.fillMaxWidth(),
        arrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
        message: MessageTheme = message(),
        messageOwn: MessageTheme = message(
            title = messageTitle(MaterialTheme.colors.primary),
            shape = messageBackgroundShape(color = MaterialTheme.colors.primary.copy(alpha = 0.4f))
        ),
        separator: TextTheme = separator(),
    ) = MessageListTheme(modifier, arrangement, message, messageOwn, separator)

    @Composable
    fun message(
        modifier: Modifier = Modifier
            .padding(16.dp, 12.dp)
            .fillMaxWidth(),
        title: TextTheme = messageTitle(),
        date: TextTheme = messageDate(),
        text: TextTheme = messageText(),
        profileImage: ProfileImageTheme = profileImage(),
        shape: ShapeTheme = messageBackgroundShape(),
        previewShape: ShapeTheme = linkPreviewShape(),
        previewImageShape: ShapeTheme = linkPreviewImageShape(),
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
    fun separator(
        modifier: Modifier = Modifier.padding(16.dp, 4.dp),
        color: Color = MaterialTheme.colors.primaryVariant,
        fontSize: TextUnit = 14.sp,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = FontWeight.Normal,
        fontFamily: FontFamily? = null,
        letterSpacing: TextUnit = TextUnit.Unspecified,
        textDecoration: TextDecoration? = null,
        textAlign: TextAlign? = null,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Ellipsis,
        softWrap: Boolean = true,
        maxLines: Int = 1,
        style: TextStyle = LocalTextStyle.current,
    ) = TextTheme(
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        style = style,
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


    @Composable
    fun shape(
        modifier: Modifier = Modifier,
        color: Color = TextFieldDefaults.textFieldColors().backgroundColor(true).value,
        padding: PaddingValues = PaddingValues(0.dp),
        shape: Shape,
    ) = ShapeTheme(shape, color, padding, modifier)

    @Composable
    fun linkPreviewShape(
        shape: Shape = RoundedCornerShape(0.dp, 0.dp, 8.dp, 8.dp),
        color: Color = MaterialTheme.colors.surface,
        padding: PaddingValues = PaddingValues(8.dp),
    ) = shape(shape = shape, padding = padding, color = color)

    @Composable
    fun linkPreviewImageShape(
        modifier: Modifier = Modifier
            .defaultMinSize(minHeight = 100.dp)
            .fillMaxWidth(),
        shape: Shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 0.dp),
        color: Color = MaterialTheme.colors.surface,
        padding: PaddingValues = PaddingValues(0.dp),
    ) = shape(modifier = modifier, shape = shape, padding = padding, color = color)

    @Composable
    fun messageBackgroundShape(
        shape: Shape = RoundedCornerShape(0.dp),
        color: Color = MaterialTheme.colors.background,
        padding: PaddingValues = PaddingValues(0.dp, 6.dp, 0.dp, 0.dp),
    ) = shape(shape = shape, padding = padding, color = color)

    @Composable
    private fun messageTitle(
        color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.high),
    ) = text(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier,
    )

    @Composable
    private fun messageDate(
        color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
    ) = text(
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier.paddingFromBaseline(bottom = 4.sp)
    )

    @Composable
    private fun messageText(
        color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.high),
    ) = text(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = color,
        modifier = Modifier,
    )
    // endregion

    @Composable
    fun input(
        shape: Shape = MaterialTheme.shapes.medium,
        colors: TextFieldColors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.background), // Workaround for text color not changes after theme switch
        ),
    ) = InputTheme(shape, colors)

    @Composable
    fun button(
        elevation: ButtonElevation? = ButtonDefaults.elevation(),
        shape: Shape = MaterialTheme.shapes.small,
        border: BorderStroke? = null,
        colors: ButtonColors = ButtonDefaults.buttonColors(),
        contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
        text: TextTheme = text(),
        modifier: Modifier = Modifier
    ) = ButtonTheme(elevation, shape, border, colors, contentPadding, text, modifier)

    @Composable
    fun icon(
        icon: ImageVector? = null,
        shape: Shape = CircleShape,
        tint: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),//LocalContentColor.current.copy(alpha = ContentAlpha.medium),
        modifier: Modifier = Modifier
            .size(40.dp)
            .padding(8.dp),
    ) = IconTheme(icon, shape, tint, modifier)

    @Composable
    fun text(
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = null,
        fontFamily: FontFamily? = null,
        letterSpacing: TextUnit = TextUnit.Unspecified,
        textDecoration: TextDecoration? = null,
        textAlign: TextAlign? = null,
        lineHeight: TextUnit = TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        style: TextStyle = LocalTextStyle.current,
    ) = TextTheme(
        modifier,
        color,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        style
    )
}
