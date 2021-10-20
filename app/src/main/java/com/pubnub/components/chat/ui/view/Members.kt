package com.pubnub.components.chat.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import com.pubnub.components.chat.ui.component.channel.LocalChannelListTheme
import com.pubnub.components.chat.ui.component.member.MemberList
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.components.chat.ui.component.presence.Presence
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.viewmodel.member.MemberViewModel
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.flow.Flow

object Members {

    @Composable
    private fun Header(
        title: String,
        onBack: () -> Unit,
    ) {
        Header(
            title = title,
            description = "",
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
        )
    }

    @Composable
    private fun Content(
        members: Flow<PagingData<MemberUi>>,
        presence: Presence? = null,
        onSelected: (MemberUi.Data) -> Unit = {},
    ) {
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        Column(modifier = Modifier.fillMaxSize()) {
            // List of channels
            MemberList(
                members = members,
                presence = presence,
                onSelected = onSelected,
                header = {
                    Column(Modifier.padding(16.dp)) {
                        // Placeholder for search bar
                        SearchView(hint = "Search members", state = textState)

                        Spacer(Modifier.height(20.dp))

                        // Header
                        val theme = LocalChannelListTheme.current
                        Text(
                            text = "Members".uppercase(),
                            fontWeight = theme.header.fontWeight,
                            fontSize = theme.header.fontSize,
                            color = theme.header.color,
                            overflow = theme.header.overflow,
                            maxLines = theme.header.maxLines,
                        )
                    }
                }
            )
        }
    }

    @Composable
    private fun Content(
        members: Map<String?, List<MemberUi.Data>>,
        presence: Presence? = null,
        onSelected: (MemberUi.Data) -> Unit = {},
    ) {
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        Column(modifier = Modifier.fillMaxSize()) {
            // List of channels
            MemberList(
                members = members,
                presence = presence,
                onSelected = onSelected,
                header = {
                    Column(Modifier.padding(16.dp)) {
                        // Placeholder for search bar
                        SearchView(hint = "Search members", state = textState)

                        Spacer(Modifier.height(20.dp))

                        // Header
                        val theme = LocalChannelListTheme.current
                        Text(
                            text = "Members".uppercase(),
                            fontWeight = theme.header.fontWeight,
                            fontSize = theme.header.fontSize,
                            color = theme.header.color,
                            overflow = theme.header.overflow,
                            maxLines = theme.header.maxLines,
                        )
                    }
                }
            )
        }
    }

    @Composable
    private fun Content(
        members: List<MemberUi.Data>,
        presence: Presence? = null,
        onSelected: (MemberUi.Data) -> Unit = {},
    ) {
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        Column(modifier = Modifier.fillMaxSize()) {
            // List of channels
            MemberList(
                members = members,
                presence = presence,
                onSelected = onSelected,
                header = {
                    Column(Modifier.padding(16.dp)) {
                        // Placeholder for search bar
                        SearchView(hint = "Search members", state = textState)

                        Spacer(Modifier.height(20.dp))

                        // Header
                        val theme = LocalChannelListTheme.current
                        Text(
                            text = "Members".uppercase(),
                            fontWeight = theme.header.fontWeight,
                            fontSize = theme.header.fontSize,
                            color = theme.header.color,
                            overflow = theme.header.overflow,
                            maxLines = theme.header.maxLines,
                        )
                    }
                }
            )
        }
    }

    @Composable
    fun View(navController: NavHostController, channelId: ChannelId) {
        val viewModel: MemberViewModel = MemberViewModel.default()

        val presence = remember { viewModel.getPresence() }
        val members = remember { viewModel.getAll(channelId = channelId) }
//        val members = remember { viewModel.getList(channelId = channelId) }
        CompositionLocalProvider(
            LocalChannel provides channelId
        ) {
            Scaffold(
                topBar = {
                    Header(
                        title = "Group members",
                        onBack = { navController.popBackStack() },
                    )
                },
                content = {

                    Content(
                        members = members,
                        presence = presence,
                        onSelected = {},
                    )
                },
            )
        }
    }
}

@Composable
@Preview
private fun MembersPreview() {
    Members.View(rememberNavController(), "channel.lobby")
}
