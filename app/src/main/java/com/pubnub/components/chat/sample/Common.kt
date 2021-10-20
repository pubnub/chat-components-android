package com.pubnub.components.chat.sample

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Component(
    label: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        if (!label.isNullOrEmpty()) Label(label)
        content()
        Divider()
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
fun Label(label: String) {
    Text(text = label.uppercase(), modifier = Modifier.padding(5.dp, 4.dp))
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}