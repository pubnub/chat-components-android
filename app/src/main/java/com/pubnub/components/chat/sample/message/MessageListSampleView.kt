package com.pubnub.components.chat.sample.message

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pubnub.components.chat.provider.DEFAULT_CHANNEL
import com.pubnub.components.chat.ui.component.message.MessageList
import com.pubnub.components.chat.viewmodel.message.MessageViewModel


@Composable
fun MessageListSampleView() {
    val context = LocalContext.current
    val viewModel: MessageViewModel = MessageViewModel.default()

    MessageList(
        messages = viewModel.getAll(),
        onMemberSelected = {
            Toast.makeText(context, "Selected member: $it", Toast.LENGTH_SHORT).show()
        },
    )
}

@Composable
fun MessageListWithHistorySampleView() {
    val context = LocalContext.current
    val channel = DEFAULT_CHANNEL
    val viewModel: MessageViewModel = MessageViewModel.defaultWithMediator(channelId = channel)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.removeAll() }) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Clear"
                )
            }
        }
    ) {
//        MemberPresenceEffect(channel) { presence ->
        MessageList(
            messages = viewModel.getAll(),
            presence = viewModel.getPresence(),
            onMemberSelected = {
                Toast.makeText(context, "Selected member: $it", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxSize(),
        )
//        }
    }
}
