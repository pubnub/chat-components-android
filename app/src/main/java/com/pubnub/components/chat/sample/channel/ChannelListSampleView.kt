package com.pubnub.components.chat.sample.channel

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.pubnub.components.chat.sample.Component
import com.pubnub.components.chat.ui.component.channel.ChannelList
import com.pubnub.components.chat.ui.component.channel.renderer.DefaultChannelRenderer.ChannelItemView
import com.pubnub.components.chat.viewmodel.channel.ChannelViewModel


@Composable
fun ChannelListSampleView() {
    val context = LocalContext.current
    val viewModel: ChannelViewModel = ChannelViewModel.default()

    Box(modifier = Modifier.fillMaxSize()) {
        ChannelList(
            channels = viewModel.getAll(),
            onSelected = {
                Toast.makeText(context, "Selected channel: ${it.id}", Toast.LENGTH_SHORT).show()
            },
        )
    }
}

@Composable
fun ChannelListGroupSampleView() {
    val context = LocalContext.current
    val viewModel: ChannelViewModel = ChannelViewModel.default()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add channel"
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ChannelList(
                channels = viewModel.getList(),
                onSelected = {
                    Toast.makeText(
                        context,
                        "Selected channel: ${it.id}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
            )
        }
    }
}

@Composable
fun ChannelItemSampleView() {
    Component("Channel Item With Icon") {
        ChannelItemView(
            title = "Company Culture",
            description = "Company culture space",
            iconUrl = "https://www.gravatar.com/avatar/ce466f2e445c38976168ba78e46?s=256&d=identicon",
            clickAction = {},
            leaveAction = {},
        )
    }

    Component("Channel Item Without Icon") {
        ChannelItemView(
            title = "Daily Standup",
            description = "Async virtual standup",
            iconUrl = "https://www.gravatar.com/avatar/2ada61db17878cd388f95da34f9?s=256&d=identicon",
            clickAction = {},
        )
    }

}
