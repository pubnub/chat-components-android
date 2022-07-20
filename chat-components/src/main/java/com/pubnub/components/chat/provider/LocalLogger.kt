package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.framework.service.error.Logger
import com.pubnub.components.chat.service.error.NoLogger

val LocalLogger = staticCompositionLocalOf<Logger> { NoLogger() }
