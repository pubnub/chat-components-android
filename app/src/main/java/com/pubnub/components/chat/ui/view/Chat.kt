package com.pubnub.components.chat.ui.view


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import com.pubnub.components.chat.R
import com.pubnub.components.chat.ui.component.input.MessageInput
import com.pubnub.components.chat.ui.component.input.renderer.AnimatedTypingIndicatorRenderer
import com.pubnub.components.chat.ui.component.message.MessageList
import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.ui.navigation.Screen
import com.pubnub.components.chat.viewmodel.channel.ChannelViewModel
import com.pubnub.components.chat.viewmodel.message.MessageViewModel
import com.pubnub.framework.data.ChannelId
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.flow.Flow

object Chat {

    @Composable
    private fun Header(
        title: String,
        description: String,
        onBack: () -> Unit,
        onClick: () -> Unit,
    ) {
        Header(
            title = title,
            description = description,
            navigationIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onBack)
                        .padding(12.dp),
                )
            },
            onTitleClick = onClick,
        )
    }

    @Composable
    private fun Content(
        messages: Flow<PagingData<MessageUi>>,
        presence: Presence?,
        onMemberSelected: (UserId) -> Unit,
    ) {
        Column(Modifier.fillMaxSize()) {
            MessageList(
                messages = messages,
                presence = presence,
                onMemberSelected = onMemberSelected,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, true),
            )

            MessageInput(
                typingIndicator = true,
                typingIndicatorRenderer = AnimatedTypingIndicatorRenderer,
            )
        }
    }

    @Composable
    fun View(navController: NavHostController, channelId: ChannelId) {
        val resources = LocalContext.current.resources

        val messageViewModel: MessageViewModel = MessageViewModel.defaultWithMediator(channelId)
        // endregion

        // region Channel View Model
        val channelViewModel: ChannelViewModel = ChannelViewModel.default()
        // endregion


        // region Header data
        val channel = remember { channelViewModel.get(channelId)!! }
        val membersCount = channel.members.size
        val title = channel.name
        val description = resources.getQuantityString(
            R.plurals.members,
            membersCount,
            membersCount
        ) + " | " + channel.description!!
        // endregion

        // region Content data
        val messages = remember { messageViewModel.getAll() }
        val presence = remember { messageViewModel.getPresence() }

        val onMemberSelected = { _: UserId -> }
        // endregion

        CompositionLocalProvider(
            LocalChannel provides channelId
        ) {

            Scaffold(
                topBar = {
                    Header(
                        title = title,
                        description = description,
                        onBack = { navController.popBackStack() },
                        onClick = { navController.navigate(Screen.Members.createRoute(channelId)) },
                    )
                },
                content = {
                    Content(
                        messages = messages,
                        presence = presence,
                        onMemberSelected = onMemberSelected,
                    )
                }
            )
        }
    }
}

@Composable
@Preview
private fun ChatPreview() {
    Chat.View(rememberNavController(), "channel.lobby")
}
