package com.pubnub.components.chat.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import com.pubnub.components.chat.R
import com.pubnub.components.chat.ui.component.channel.ChannelList
import com.pubnub.components.chat.ui.component.channel.ChannelUi
import com.pubnub.components.chat.ui.component.member.ProfileImage
import com.pubnub.components.chat.ui.navigation.Screen
import com.pubnub.components.chat.viewmodel.channel.ChannelViewModel
import com.pubnub.components.chat.viewmodel.member.MemberViewModel
import com.pubnub.components.repository.util.Sorted
import kotlinx.coroutines.flow.Flow

object Dashboard {

    @Composable
    private fun Header(
        imageUrl: String,
        isOnline: Boolean? = null,
    ) {
        Header(
            title = stringResource(id = R.string.dashboard_title),
            actions = {
                ProfileImage(
                    imageUrl = imageUrl,
                    isOnline = isOnline,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(40.dp),
                )
            },
        )
    }

    @Composable
    private fun Content(
        channels: Flow<PagingData<ChannelUi>>,
        onSelected: (ChannelUi.Data) -> Unit,
        onAdd: () -> Unit,
    ) {
        // todo: should be moved to parent
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        Column(modifier = Modifier.fillMaxSize()) {
            // List of channels
            ChannelList(
                channels = channels,
                onSelected = onSelected,
                onAdd = onAdd,
                header = {
                    // Placeholder for search bar
                    Box(Modifier.padding(16.dp)) {
                        SearchView(state = textState)
                    }
                }
            )
        }
    }

    @Composable
    fun View(navController: NavHostController) {
        val viewModel: ChannelViewModel = ChannelViewModel.default()
        val memberViewModel: MemberViewModel = MemberViewModel.default()

        val channels = remember {
            viewModel.getAll(
                sorted = arrayOf(
                    Sorted("type", Sorted.Direction.ASC),
                )
            )
        }
        val currentUser = remember { memberViewModel.getMember()!! }

        Scaffold(
            topBar = {
                Header(
                    imageUrl = currentUser.profileUrl!!,
                    isOnline = true
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { },
                    text = {
                        Text(
                            text = stringResource(id = R.string.chat_new_button),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(id = R.string.chat_new_button),
                        )
                    }
                )
            },
            content = {
                Content(
                    channels = channels,
                    onSelected = {
                        navController.navigate(Screen.Channel.createRoute(it.id))
                    },
                    onAdd = {}
                )
            },
        )
    }
}

@Composable
@Preview
private fun DashboardPreview() {
    Dashboard.View(rememberNavController())
}
