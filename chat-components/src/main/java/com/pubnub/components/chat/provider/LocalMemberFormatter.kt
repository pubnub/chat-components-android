package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.chat.ui.component.member.MemberUi
import com.pubnub.framework.data.UserId

val LocalMemberFormatter =
    staticCompositionLocalOf<(UserId) -> MemberUi.Data> { throw MemberFormatterNotInitializedException() }

class MemberFormatterNotInitializedException :
    Exception("MemberFormatter repository not initialized")
