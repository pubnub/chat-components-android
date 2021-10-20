package com.pubnub.components.chat.ui.component.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.pubnub.components.chat.ui.R

@Composable
fun MessageInput(
    placeholder: String = stringResource(id = R.string.type_message),
    initialText: String = "",
//    senderInfo: Boolean = false,
//    typingIndicator: Boolean = false,
    onSent: (String) -> Unit,
    onChange: (String) -> Unit,
) {
    val theme = LocalMessageInputTheme.current
    val context = LocalContext.current

    var text by rememberSaveable { mutableStateOf(initialText) }

    val sendAction: (String) -> Unit = {
        // reset input
        val message = it
        text = ""

        // call action
        onSent(message)
    }

    // LocalTextStyle
    Row(modifier = theme.modifier) {
        TextField(
            value = text,
            onValueChange = { text = it; onChange(it) },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = context.getString(R.string.message_input_text) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = { sendAction(text) }),
            shape = theme.input.shape,
            singleLine = true,
            trailingIcon = {
                Row {
                    SendButton(
                        enabled = text.isNotBlank(),
                        action = { sendAction(text) },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            },
            colors = theme.input.colors,
        )
    }
}

@Composable
fun SendButton(enabled: Boolean = true, action: () -> Unit) {
    val theme = LocalMessageInputTheme.current
    val context = LocalContext.current
    Button(
        onClick = action,
        enabled = enabled,
        elevation = theme.button.elevation,
        shape = theme.button.shape,
        border = theme.button.border,
        colors = theme.button.colors,
        contentPadding = theme.button.contentPadding,
        modifier = theme.button.modifier.semantics {
            contentDescription = context.getString(R.string.message_input_button)
        },
    ) {
        Text(
            text = stringResource(id = R.string.send),
            modifier = theme.button.text.modifier,
            color = theme.button.text.color,
            fontSize = theme.button.text.fontSize,
            fontStyle = theme.button.text.fontStyle,
            fontWeight = theme.button.text.fontWeight,
            fontFamily = theme.button.text.fontFamily,
            letterSpacing = theme.button.text.letterSpacing,
            textDecoration = theme.button.text.textDecoration,
            textAlign = theme.button.text.textAlign,
            lineHeight = theme.button.text.lineHeight,
            overflow = theme.button.text.overflow,
            softWrap = theme.button.text.softWrap,
            maxLines = theme.button.text.maxLines,
            style = theme.button.text.style,
        )
    }
}