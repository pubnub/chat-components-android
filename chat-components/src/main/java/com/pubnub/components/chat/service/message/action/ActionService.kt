package com.pubnub.components.chat.service.message.action

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.framework.service.ActionService

val LocalActionService =
    compositionLocalOf<ActionService> { throw ActionServiceNotInitializedException() }

class ActionServiceNotInitializedException : Exception("Action Service not initialized")
