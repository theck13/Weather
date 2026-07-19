package com.pranshulgg.weather_master_app.feature.settings.appearance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet
import com.pranshulgg.weather_master_app.core.utils.extensions.times

@OptIn(
    ExperimentalMaterial3Api::class,
)
@Composable
fun ColorPickerItem() {
    var isSheetOpen by remember { mutableStateOf(false) }
    val preferences = LocalAppPrefs.current
    var selectedColor = preferences.customThemeColor
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Surface(
        shape = RoundedCornerShape(
            size = TouchMinimum,
        ),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(
                        color = preferences.customThemeColor.toColorInt(),
                    )
                )
                .clickable(
                    onClick = {
                        isSheetOpen = true
                    }
                )
                .height(
                    height = TouchMinimum * 0.75,
                )
                .width(
                    width = TouchMinimum * 0.50,
                ),
        ) {}
    }

    if (isSheetOpen) {
        ActionBottomSheet(
            onCancel = {
                isSheetOpen = false
            },
            onConfirm = {
                preferences.setCustomThemeColor(selectedColor)
            },
            sheetState = sheetState,
        ) {
            ColorPicker(
                onThemeColorChanged = { color ->
                    selectedColor = color
                },
            )
        }
    }
}
