package com.pubnub.components.chat.ui.component.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
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

class TextTheme(
    modifier: Modifier = Modifier,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    letterSpacing: TextUnit,
    textDecoration: TextDecoration?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
    overflow: TextOverflow,
    softWrap: Boolean,
    maxLines: Int,
    // TODO: 5/25/21 remove text style!
    style: TextStyle,
) {
    var modifier by mutableStateOf(modifier, structuralEqualityPolicy())
        internal set

    var color by mutableStateOf(color, structuralEqualityPolicy())
        internal set

    var fontSize by mutableStateOf(fontSize, structuralEqualityPolicy())
        internal set

    var fontStyle by mutableStateOf(fontStyle, structuralEqualityPolicy())
        internal set

    var fontWeight by mutableStateOf(fontWeight, structuralEqualityPolicy())
        internal set

    var fontFamily by mutableStateOf(fontFamily, structuralEqualityPolicy())
        internal set

    var letterSpacing by mutableStateOf(letterSpacing, structuralEqualityPolicy())
        internal set

    var textDecoration by mutableStateOf(textDecoration, structuralEqualityPolicy())
        internal set

    var textAlign by mutableStateOf(textAlign, structuralEqualityPolicy())
        internal set

    var lineHeight by mutableStateOf(lineHeight, structuralEqualityPolicy())
        internal set

    var overflow by mutableStateOf(overflow, structuralEqualityPolicy())
        internal set

    var softWrap by mutableStateOf(softWrap, structuralEqualityPolicy())
        internal set

    var maxLines by mutableStateOf(maxLines, structuralEqualityPolicy())
        internal set

    var style by mutableStateOf(style, structuralEqualityPolicy())
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextTheme

        if (modifier != other.modifier) return false
        if (color != other.color) return false
        if (fontSize != other.fontSize) return false
        if (fontStyle != other.fontStyle) return false
        if (fontWeight != other.fontWeight) return false
        if (fontFamily != other.fontFamily) return false
        if (letterSpacing != other.letterSpacing) return false
        if (textDecoration != other.textDecoration) return false
        if (textAlign != other.textAlign) return false
        if (lineHeight != other.lineHeight) return false
        if (overflow != other.overflow) return false
        if (softWrap != other.softWrap) return false
        if (maxLines != other.maxLines) return false
        if (style != other.style) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modifier.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + fontSize.hashCode()
        result = 31 * result + (fontStyle?.hashCode() ?: 0)
        result = 31 * result + (fontWeight?.hashCode() ?: 0)
        result = 31 * result + (fontFamily?.hashCode() ?: 0)
        result = 31 * result + letterSpacing.hashCode()
        result = 31 * result + (textDecoration?.hashCode() ?: 0)
        result = 31 * result + (textAlign?.hashCode() ?: 0)
        result = 31 * result + lineHeight.hashCode()
        result = 31 * result + overflow.hashCode()
        result = 31 * result + softWrap.hashCode()
        result = 31 * result + maxLines
        result = 31 * result + style.hashCode()
        return result
    }

    fun asStyle() = TextStyle(
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
    )
}
