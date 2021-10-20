package com.pubnub.components.chat.ui.navigation

import com.pubnub.framework.data.ChannelId

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")

    object Channel : Screen("channel/{channelId}") {
        fun createRoute(channelId: ChannelId) = "channel/$channelId"
    }

    object Members : Screen("channel/{channelId}/members") {
        fun createRoute(channelId: String) = "channel/$channelId/members"
    }

    object NewChat : Screen("channel/create")
}