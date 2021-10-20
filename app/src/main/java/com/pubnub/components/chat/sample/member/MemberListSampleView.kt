package com.pubnub.components.chat.sample.member

import android.widget.Toast
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pubnub.components.chat.ui.component.member.MemberList
import com.pubnub.components.chat.ui.component.member.renderer.DefaultMemberRenderer.MemberItemView
import com.pubnub.components.chat.viewmodel.member.MemberViewModel


@Composable
fun MemberListSampleView() {
    val context = LocalContext.current
    val viewModel: MemberViewModel = MemberViewModel.default()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add member"
                )
            }
        }
    ) {
        MemberList(
            members = viewModel.getAll(),
            presence = viewModel.getPresence(),
            onSelected = {
                Toast.makeText(context, "Selected member: ${it.id}", Toast.LENGTH_SHORT).show()
            },
        )
    }
}

@Composable
fun MemberListWithHeadersSampleView() {
    val context = LocalContext.current
    val viewModel: MemberViewModel = MemberViewModel.default()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add member"
                )
            }
        }
    ) {
        MemberList(
            members = viewModel.getList(),
            presence = viewModel.getPresence(),
            onSelected = {
                Toast.makeText(context, "Selected member: ${it.id}", Toast.LENGTH_SHORT).show()
            },
        )
    }
}


@Composable
fun MemberItemSampleView() {
    MemberItemView(
        name = "Mark Kelley (You)",
        description = "Office Assistant",
        profileUrl = "https://randomuser.me/api/portraits/men/1.jpg",
        clickAction = {},
    )
}

@Composable
fun MemberItemWithPresenceSampleView(online: Boolean) {
    MemberItemView(
        name = "Mark Kelley (You)",
        description = "Office Assistant",
        profileUrl = "https://randomuser.me/api/portraits/men/1.jpg",
        online = online,
        clickAction = {},
    )
}
