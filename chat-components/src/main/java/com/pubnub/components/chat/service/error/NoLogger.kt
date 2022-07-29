package com.pubnub.components.chat.service.error

import com.pubnub.framework.service.error.Logger

class NoLogger : Logger {
    override fun log(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
        // not used
    }
}
