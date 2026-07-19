package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun EmptyContainerPlaceholder(
    icon: Int,
    description: String = "",
    fraction: Float = 0.80f,
    isLandscape: Boolean,
    paddingValues: PaddingValues,
    size: Float = 1.00f,
    text: String,
) {
    val containerSize = 160.dp * size
    val descSize = 16.sp * size
    val iconSize = 76.dp * size
    val spacingLarge = 16.dp * size
    val spacingSmall = 5.dp * size
    val titleSize = 24.sp * size

    Column(
        modifier = Modifier
            .fillMaxHeight(
                fraction = fraction,
            )
            .fillMaxWidth()
            .padding(
                paddingValues = paddingValues,
            )
            .padding(
                top = if (isLandscape) 16.dp else 96.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Surface(
            modifier = Modifier
                .height(
                    height = containerSize,
                )
                .width(
                    width = containerSize,
                ),
            color = MaterialTheme.colorScheme.surfaceBright,
            shape = MaterialShapes.Pill.toShape(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Symbol(
                    color = MaterialTheme.colorScheme.primary,
                    icon = icon,
                    size = iconSize,
                )
            }
        }

        Gap(
            vertical = spacingLarge,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = titleSize,
            fontWeight = FontWeight.Black,
            text = text
        )

        if (description != "") {
            Gap(
                vertical = spacingSmall,
            )

            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = descSize,
                text = description,
                textAlign = TextAlign.Center,
            )
        }
    }
}
