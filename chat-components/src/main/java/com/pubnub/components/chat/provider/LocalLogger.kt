package com.pubnub.components.chat.provider

import androidx.compose.runtime.compositionLocalOf
import com.pubnub.components.chat.service.error.TimberLogger
import com.pubnub.framework.service.error.Logger

val LocalLogger = compositionLocalOf<Logger> { TimberLogger() }
