package com.pubnub.components.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.components.chat.provider.PubNubPreview
import com.pubnub.components.chat.sample.Label
import com.pubnub.components.chat.sample.channel.ChannelItemSampleView
import com.pubnub.components.chat.sample.channel.ChannelListGroupSampleView
import com.pubnub.components.chat.sample.channel.ChannelListSampleView
import com.pubnub.components.chat.sample.input.ImageInputPubNubSampleView
import com.pubnub.components.chat.sample.input.LinkInputPubNubSampleView
import com.pubnub.components.chat.sample.input.MessageInputPubNubSampleView
import com.pubnub.components.chat.sample.input.MessageInputSampleView
import com.pubnub.components.chat.sample.member.MemberItemSampleView
import com.pubnub.components.chat.sample.member.MemberItemWithPresenceSampleView
import com.pubnub.components.chat.sample.member.MemberListSampleView
import com.pubnub.components.chat.sample.member.MemberListWithHeadersSampleView
import com.pubnub.components.chat.sample.message.MessageListSampleView
import com.pubnub.components.chat.sample.message.MessageListWithHistorySampleView
import com.pubnub.components.chat.ui.theme.AppTheme
import com.pubnub.components.chat.util.CoilHelper

private val pubNub: PubNub = PubNub(
    PNConfiguration().apply {
        this.subscribeKey = "sub-c-3f74fe58-b3ef-11eb-8cd6-ee35b8e5702f"
        this.publishKey = "pub-c-8e3d83a5-63e2-4290-9c57-4164137fe297"
        this.uuid = "user_63ea15931d8541a3bd35e5b1f09087dc"
        this.logVerbosity = PNLogVerbosity.BODY
    }
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoilHelper.setCoil(this)
        setContent {
            Samples(components)
        }
    }
}

private val components: Array<Pair<String, @Composable ColumnScope.() -> Unit>>
    @Composable get() = arrayOf(
        "Message Input" to { MessageInputSampleView(); MessageInputPubNubSampleView(); LinkInputPubNubSampleView(); ImageInputPubNubSampleView() },
        "Channel Item" to { ChannelItemSampleView() },
        "Channel List" to { ChannelListSampleView() },
        "Channel List Group" to { ChannelListGroupSampleView() },
        "Member Item" to {
            MemberItemSampleView(); MemberItemWithPresenceSampleView(true); MemberItemWithPresenceSampleView(
            false
        )
        },
        "Member List" to { MemberListSampleView() },
        "Member List Groups" to { MemberListWithHeadersSampleView() },
        "Message List" to { MessageListSampleView() },
        "Message List Sync" to { MessageListWithHistorySampleView() },
    )

@Composable
fun Samples(
    components: Array<Pair<String, @Composable() (ColumnScope.() -> Unit)>>,
    isDark: Boolean = false
) {
    var darkTheme by rememberSaveable { mutableStateOf(isDark) }
    var selectedIndex by rememberSaveable { mutableStateOf(components.size - 1) }

    AppTheme(darkTheme = darkTheme, pubNub = pubNub) {
        Surface(color = MaterialTheme.colors.background) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Day / Night Theme Switch
                SwitchComponent(darkTheme) { enabled ->
                    darkTheme = enabled
                }

                // Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedIndex,
                ) {
                    components.forEachIndexed { index, view ->
                        Tab(
                            modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 8.dp),
                            selected = index == selectedIndex,
                            onClick = { selectedIndex = index }
                        )
                        {
                            Label(view.first)
                        }
                    }
                }
                // Components content
                components[selectedIndex].second(this)
            }
        }
    }
}

@Composable
fun SwitchComponent(checked: Boolean, onChange: (Boolean) -> Unit) {

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Label("Theme: ${if (checked) "Dark" else "Light"}")
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PubNubPreview {
        Samples(components = components)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewDark() {
    PubNubPreview {
        Samples(components = components, isDark = true)
    }
}
