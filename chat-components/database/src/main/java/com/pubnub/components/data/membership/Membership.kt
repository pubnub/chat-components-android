package com.pubnub.components.data.membership

interface Membership {
    val id: String
    val channelId: String
    val memberId: String
    val status: String?
}
