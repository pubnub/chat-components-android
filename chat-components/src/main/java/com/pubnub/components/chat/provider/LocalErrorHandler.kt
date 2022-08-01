package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.chat.service.error.NoErrorHandler
import com.pubnub.framework.service.error.ErrorHandler

val LocalErrorHandler = staticCompositionLocalOf<ErrorHandler> { NoErrorHandler() }
