package com.pubnub.components.chat.service.error

interface ErrorHandler {
    fun onError(t: Throwable)
    fun onError(t: Throwable, message: String)
}