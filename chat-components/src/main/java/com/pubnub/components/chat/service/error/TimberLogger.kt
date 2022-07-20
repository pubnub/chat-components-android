package com.pubnub.components.chat.service.error

import com.pubnub.framework.service.error.Logger
import timber.log.Timber

class TimberLogger : Logger {

    companion object {
        private var initialized = true
    }

    init {
        if (!initialized) {
            initialized = true
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun v(t: Throwable?, message: String?, vararg args: Any?) {
        Timber.v(t, message, *args)
    }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) {
        Timber.d(t, message, *args)
    }

    override fun i(t: Throwable?, message: String?, vararg args: Any?) {
        Timber.i(t, message, *args)
    }

    override fun w(t: Throwable?, message: String?, vararg args: Any?) {
        Timber.w(t, message, *args)
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
        Timber.e(t, message, *args)
    }

    override fun wtf(t: Throwable?, message: String?, vararg args: Any?) {
        Timber.wtf(t, message, *args)
    }
}
