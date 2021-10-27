package com.pubnub.components.chat.ui.component.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.api.PubNub


val LocalPubNub = compositionLocalOf<PubNub> { throw MissingPubNubException() }

class MissingPubNubException :
    Exception("No PubNub instance provided.")

class MissingThemeException :
    Exception("No theme provided. Did you forget to call `PubNubProvider { … }`?")
