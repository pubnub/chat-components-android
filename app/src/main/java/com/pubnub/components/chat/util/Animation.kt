package com.pubnub.components.chat.util

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.pubnub.components.chat.ui.navigation.Screen

@OptIn(ExperimentalAnimationApi::class)
object Animation {

    private const val DURATION = 200

    object Dashboard : NavHostAnimation(
        enterTransition = { initial, _ ->
            when (initial.destination.route) {
                Screen.Channel.route -> slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(DURATION)
                )
                else -> null
            }
        },
        exitTransition = { _, target ->
            when (target.destination.route) {
                Screen.Channel.route -> slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(DURATION)
                )
                else -> null
            }
        },
    )

    object Channel : NavHostAnimation(
        enterTransition = { initial, _ ->
            when (initial.destination.route) {
                Screen.Dashboard.route -> slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(DURATION)
                )
                Screen.Members.route -> slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(DURATION)
                )
                else -> null
            }
        },
        exitTransition = { _, target ->
            when (target.destination.route) {
                Screen.Dashboard.route -> slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(DURATION)
                )
                Screen.Members.route -> slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(DURATION)
                )
                else -> null
            }
        }
    )

    object Members : NavHostAnimation(
        enterTransition = { initial, _ ->
            when (initial.destination.route) {
                Screen.Channel.route -> slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(DURATION)
                )
                else -> null
            }
        },
        exitTransition = { _, target ->
            when (target.destination.route) {
                Screen.Channel.route -> slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(DURATION)
                )
                else -> null
            }
        },
    )
}

@OptIn(ExperimentalAnimationApi::class)
abstract class NavHostAnimation(
    val enterTransition: (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)? = null,
    val exitTransition: (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)? = null,
    val popEnterTransition: (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> EnterTransition?)? = enterTransition,
    val popExitTransition: (AnimatedContentScope<String>.(initial: NavBackStackEntry, target: NavBackStackEntry) -> ExitTransition?)? = exitTransition,
)

