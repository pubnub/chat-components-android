package com.pubnub.components.chat.service.message.action

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.framework.service.ActionService

val LocalActionService =
    staticCompositionLocalOf<ActionService> { throw ActionServiceNotInitializedException() }

class ActionServiceNotInitializedException : Exception("Action Service not initialized")