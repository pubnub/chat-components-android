package com.pubnub.components.chat.ui.component.message

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

// Regex containing the syntax tokens
val urlPattern by lazy {
    Regex("((http|https)://)?(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)")
}

// Accepted annotations for the ClickableTextWrapper
enum class SymbolAnnotationType {
    LINK
}

typealias StringAnnotation = AnnotatedString.Range<String>
// Pair returning styled content and annotation for ClickableText when matching syntax token
typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>

/**
 * Format a message
 * | matching urlPattern... -> clickable link, opening it into the browser
 *
 * @param text contains message to be parsed
 * @return AnnotatedString with annotations used inside the ClickableText wrapper
 */
@Composable
fun messageFormatter(
    text: String,
): AnnotatedString {
    val tokens = urlPattern.findAll(text)

    return buildAnnotatedString {

        var cursorPosition = 0

        for (token in tokens) {
            append(text.slice(cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                matchResult = token,
            )
            append(annotatedString)

            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }
}

/**
 * Map regex matches found in a message with supported syntax symbols
 *
 * @param matchResult is a regex result matching our syntax symbols
 * @return pair of AnnotatedString with annotation (optional) used inside the ClickableText wrapper
 */
private fun getSymbolAnnotation(
    matchResult: MatchResult,
): SymbolAnnotation {
    return SymbolAnnotation(
        AnnotatedString(
            text = matchResult.value,
            spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
        ),
        StringAnnotation(
            item = matchResult.value,
            start = matchResult.range.first,
            end = matchResult.range.last,
            tag = SymbolAnnotationType.LINK.name
        )
    )
}

@Preview
@Composable
private fun LinkFormatPreview() {
    val content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n" +
            "Mauris sed feugiat nunc, scelerisque lacinia mi. \n" +
            "www.pubnub.com \n" +
            "Donec pretium, sem non \n" +
            "laoreet euismod, nibh eros congue leo, sit amet iaculis mi lectus \n" +
            "vel sapien. \n" +
            "pubnub.com \n" +
            "Suspendisse faucibus faucibus arcu, at viverra ex vestibulum nec. \n" +
            "https://pubnub.com?34535/534534?dfg=g&fg \n" +
            "Pellentesque habitant morbi tristique senectus et netus et malesuada fames \n" +
            "ac turpis egestas. \n" +
            "http://PubNub.com?2rjl6 \n" +
            "Class aptent taciti sociosqu ad litora torquent per conubia \n" +
            "nostra, per inceptos himenaeos. \n" +
            "pubnub.com/docs/ \n" +
            "Vestibulum eget dui augue. Maecenas lacus est, \n" +
            "placerat eget blandit vel, maximus vel tellus. Aliquam dignissim sapien sit amet \n" +
            "justo blandit iaculis."
    Text(text = messageFormatter(text = content))
}