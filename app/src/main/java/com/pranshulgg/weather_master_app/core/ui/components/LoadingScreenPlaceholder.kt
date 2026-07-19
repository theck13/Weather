package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun LoadingScreenPlaceholder(
    fraction: Float = 1.00f,
    paddingValues: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight(
                fraction = fraction,
            )
            .fillMaxWidth()
            .padding(
                paddingValues = paddingValues,
            )
            .zIndex(
                zIndex = 10000.00f,
            ),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(
            modifier = Modifier.size(
                size = 60.dp,
            ),
        )
    }
}
