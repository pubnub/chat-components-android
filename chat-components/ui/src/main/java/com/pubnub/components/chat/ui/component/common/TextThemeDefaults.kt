package com.pubnub.components.chat.ui.component.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

object TextThemeDefaults {
    @Composable
    fun title(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.high),
        fontSize: TextUnit = 16.sp,
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

    @Composable
    fun subtitle(
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
        fontSize: TextUnit = 14.sp,
        fontStyle: FontStyle? = null,
        fontWeight: FontWeight? = null,
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

    @Composable
    fun header(
        modifier: Modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .padding(16.dp, 24.dp, 16.dp, 16.dp)
            .fillMaxWidth(),
        color: Color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.high),
        fontSize: TextUnit = 18.sp,
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
