package com.pubnub.components.chat.provider

import androidx.compose.runtime.staticCompositionLocalOf
import com.pubnub.components.chat.service.error.TimberLogger
import com.pubnub.framework.service.error.Logger

val LocalLogger = staticCompositionLocalOf<Logger> { TimberLogger() }
