package com.heckofanapp.weather.feature.shared.ui

import androidx.compose.runtime.Composable
import com.heckofanapp.weather.core.ui.components.TextAlertDialog

object SharedDialogs {
    @Composable
    fun DeviceLocationPermissionInfoDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        show: Boolean,
    ) {
        TextAlertDialog(
            message = "Allow location access to save places using your current position.  Your location will only be shared with the sources you choose.",
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            show = show,
            title = "Location Permission",
        )
    }

    @Composable
    fun DeviceBackgroundLocationPermissionInfoDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        show: Boolean,
    ) {
        TextAlertDialog(
            message = "Allow background location access to keep your saved device location updated even when the app is closed or running in the background so widgets stay up-to-date.",
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            show = show,
            title = "Background Location Permission",
        )
    }
}
