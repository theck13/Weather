package com.heckofanapp.weather.feature.settings.background

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.snackbar.SnackbarManager

object BatteryOptimizationHelper {
    fun requestDisableBatteryOptimization(
        context: Context,
    ) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        if (powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            SnackbarManager.show(
                message = R.string.info_already_ignoring_battery_opt,
            )
            return
        }

        val fallback = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(fallback)
    }
}