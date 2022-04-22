package com.pubnub.components.chat.ui.component.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.api.PubNub
import com.pubnub.framework.data.UserId


val LocalUser = compositionLocalOf<UserId> { throw MissingUserException() }

class MissingUserException :
    Exception("No UserId provided.")
