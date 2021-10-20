package com.pubnub.components.chat

import android.app.Application
import com.pubnub.components.chat.util.DatabaseHelper
import timber.log.Timber

class ChatComponentsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        DatabaseHelper.initialize(this)
    }
}
