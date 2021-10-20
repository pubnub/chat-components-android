package com.pubnub.components.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.components.chat.ui.navigation.Screen
import com.pubnub.components.chat.ui.theme.AppTheme
import com.pubnub.components.chat.ui.view.Chat
import com.pubnub.components.chat.ui.view.Dashboard
import com.pubnub.components.chat.ui.view.Members
import com.pubnub.components.chat.util.Animation
import com.pubnub.components.chat.util.CoilHelper
import com.pubnub.components.chat.util.DatabaseHelper
import com.pubnub.components.data.Database
import com.pubnub.framework.data.UserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
class ChatActivity : ComponentActivity() {
    private lateinit var pubNub: PubNub

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val initialized = initializeDatabase()

            AnimatedVisibility(
                visible = initialized,
                enter = fadeIn(animationSpec = tween(700)),
                exit = fadeOut(animationSpec = tween(700))
            ) {
                if (::pubNub.isInitialized) {
                    AppTheme(pubNub = pubNub) {
                        val navController = rememberAnimatedNavController()
                        AnimatedNavHost(navController, startDestination = Screen.Dashboard.route) {
                            composable(
                                route = Screen.Dashboard.route,
                                enterTransition = Animation.Dashboard.enterTransition,
                                exitTransition = Animation.Dashboard.exitTransition,
                            ) {
                                Dashboard.View(navController)
                            }
                            composable(
                                route = Screen.Channel.route,
                                enterTransition = Animation.Channel.enterTransition,
                                exitTransition = Animation.Channel.exitTransition,
                            ) { backStackEntry ->
                                val channelId = backStackEntry.arguments!!.getString("channelId")!!
                                Chat.View(navController, channelId)
                            }
                            composable(
                                route = Screen.Members.route,
                                enterTransition = Animation.Members.enterTransition,
                                exitTransition = Animation.Members.exitTransition,
                            ) { backStackEntry ->
                                val channelId = backStackEntry.arguments!!.getString("channelId")!!
                                Members.View(navController, channelId)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroy()
    }

    private fun initialize(userId: UserId) {
        Timber.e("Initialize PubNub")
        pubNub = createPubNub(
            publishKey = "pub-c-8e3d83a5-63e2-4290-9c57-4164137fe297",
            subscribeKey = "sub-c-3f74fe58-b3ef-11eb-8cd6-ee35b8e5702f",
            userId = userId,
//            filter = "uuid!='${randomUser.id}'", // filtering messages
        )
        CoilHelper.setCoil(this)

    }

    private fun createPubNub(
        publishKey: String,
        subscribeKey: String,
        userId: String? = null,
        cipherKey: String? = null,
        filter: String? = null,
    ): PubNub {
        val config = PNConfiguration().apply {
            this.subscribeKey = subscribeKey
            this.publishKey = publishKey
            if (!cipherKey.isNullOrBlank()) this.cipherKey = cipherKey
            if (!userId.isNullOrBlank()) this.uuid = userId
            this.logVerbosity = PNLogVerbosity.BODY
            if (!filter.isNullOrBlank()) this.filterExpression = filter
        }
        return PubNub(config)
    }

    private fun destroy() {
        pubNub.destroy()
    }

    @Composable
    private fun initializeDatabase(): Boolean {
        // Workaround for database prepopulate
        val ready by DatabaseHelper.isReady()
        val (initialized, setInitialized) = remember { mutableStateOf(false) }

        if (ready) {
            LaunchedEffect(ready) {
                withContext(Dispatchers.IO) {
                    val member = Database.INSTANCE.memberDao().getList().random()
                    initialize(member.id)
                    delay(100L)
                    setInitialized(true)
                }
            }
        }

        return initialized
    }
}

