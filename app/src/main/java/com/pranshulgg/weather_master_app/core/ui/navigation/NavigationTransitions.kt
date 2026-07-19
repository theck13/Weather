package com.pranshulgg.weather_master_app.core.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
object NavigationTransitions {
    private const val FADE_IN = 350
    private const val FADE_OUT = 200

    fun enter(): EnterTransition =
        slideInHorizontally(
            animationSpec = tween(
                durationMillis = FADE_IN,
            ),
            initialOffsetX = { 1 * it },
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = FADE_IN,
            ),
        )

    fun exit(): ExitTransition =
        slideOutHorizontally(
            animationSpec = tween(
                durationMillis = FADE_OUT,
            ),
            targetOffsetX = { 1 * -it / 4 },
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = FADE_OUT,
            ),
        )

    fun popEnter(): EnterTransition =
        slideInHorizontally(
            animationSpec = tween(
                durationMillis = FADE_IN,
            ),
            initialOffsetX = { 1 * -it / 4 },
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = FADE_IN,
            ),
        )

    fun popExit(): ExitTransition =
        slideOutHorizontally(
            animationSpec = tween(
                durationMillis = FADE_OUT,
            ),
            targetOffsetX = { 1 * it },
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = FADE_OUT,
            ),
        )
}
