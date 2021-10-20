package com.pubnub.components.chat.service.error

import timber.log.Timber

class TimberErrorHandler : ErrorHandler {

    companion object {
        private var initialized = true
    }

    init {
        if (!initialized) {
            initialized = true
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onError(t: Throwable) {
        Timber.e(t)
    }

    override fun onError(t: Throwable, message: String) {
        Timber.e(t, message)
    }
}