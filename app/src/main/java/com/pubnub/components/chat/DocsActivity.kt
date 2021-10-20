package com.pubnub.components.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.components.chat.ui.component.channel.ChannelList
import com.pubnub.components.chat.viewmodel.channel.ChannelViewModel

class DocsActivity : ComponentActivity() {

    private val pubNub: PubNub = PubNub(
        PNConfiguration().apply {
            this.publishKey = "pub-c-key"
            this.subscribeKey = "sub-c-key"
            this.uuid = "uuid-of-current-user"
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ChannelViewModel = ChannelViewModel.default()

            Box(modifier = Modifier.fillMaxSize()) {
                ChannelList(
                    channels = viewModel.getAll(),
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pubNub.destroy()
    }
}