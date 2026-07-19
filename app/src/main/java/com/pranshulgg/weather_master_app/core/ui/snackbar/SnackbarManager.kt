package com.pranshulgg.weather_master_app.core.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarManager {
    data class SnackbarEvent(
        val action: Int? = null,
        val arguments: Any? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val messageResource: Int? = null,
        val messageText: String? = null,
        val onAction: (() -> Unit)? = null,
    )

    private val _events = MutableSharedFlow<SnackbarEvent>(
        extraBufferCapacity = 1,
    )

    val events = _events.asSharedFlow()

    fun show(
        action: Int? = null,
        arguments: Any? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        message: Int,
        onAction: (() -> Unit)? = null,
    ) {
        _events.tryEmit(
            SnackbarEvent(
                action = action,
                arguments = arguments,
                duration = duration,
                messageResource = message,
                onAction = onAction,
            )
        )
    }

    fun show(
        action: Int? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        message: String,
        onAction: (() -> Unit)? = null,
    ) {
        _events.tryEmit(
            SnackbarEvent(
                action = action,
                duration = duration,
                messageText = message,
                onAction = onAction,
            )
        )
    }
}
