package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SettingsTileIcon(
    icon: Int,
    isError: Boolean = false,
) {
    Symbol(
        color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        icon = icon,
    )
}
