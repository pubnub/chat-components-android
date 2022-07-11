package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.framework.service.error.ErrorHandler
import com.pubnub.components.chat.service.error.NoErrorHandler

val LocalErrorHandler = staticCompositionLocalOf<ErrorHandler> { NoErrorHandler() }
