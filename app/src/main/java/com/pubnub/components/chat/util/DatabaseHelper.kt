package com.pubnub.components.chat.util

import android.content.Context
import androidx.compose.runtime.*
import com.pubnub.components.chat.data.prepopulate
import com.pubnub.components.data.Database
import timber.log.Timber

object DatabaseHelper {
    private var initialized by mutableStateOf(false)


    fun initialize(applicationContext: Context) {
        Timber.e("Initialized set to false")
        initialized = false
        Database.initialize(applicationContext) { database ->
            database.prepopulate(applicationContext) {
                initialized = true
            }.fallbackToDestructiveMigration()
        }
    }

    @Composable
    fun isReady(): State<Boolean> {
        val ready = remember { mutableStateOf(false) }
        LaunchedEffect(initialized) {
            Database.INSTANCE.memberDao().size()
            if (initialized) {
                ready.value = true
            }
        }
        return ready
    }
}