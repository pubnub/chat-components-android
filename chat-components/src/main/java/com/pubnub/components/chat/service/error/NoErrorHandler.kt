package com.pubnub.components.chat.service.error

class NoErrorHandler : ErrorHandler {
    override fun onError(t: Throwable) {}
    override fun onError(t: Throwable, message: String) {}
}