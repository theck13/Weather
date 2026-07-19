package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pranshulgg.weather_master_app.R

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun ErrorContainer(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    errorDescription: String = "Something went wrong",
    onRetry: () -> Unit = {},
    showRetryAction: Boolean = true,
) {
    Column(
        modifier = Modifier
            .background(
                color = containerColor,
            )
            .fillMaxSize()
            .padding(
                all = 16.dp,
            )
            .zIndex(
                zIndex = 100000.00f,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Symbol(
            color = MaterialTheme.colorScheme.error,
            icon = R.drawable.ic_info_24,
            size = 54.dp,
        )

        Gap(
            vertical = 26.dp,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            text = "Error occurred",
        )

        Gap(
            vertical = 8.dp,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            text = errorDescription,
        )

        if (showRetryAction) {
            Gap(
                vertical = 34.dp,
            )

            ExpressiveButton(
                icon = R.drawable.ic_refresh_24,
                onClick = {
                    onRetry()
                },
                size = ButtonDefaults.MediumContainerHeight,
                text = "Try again",
            )
        }
    }
}
