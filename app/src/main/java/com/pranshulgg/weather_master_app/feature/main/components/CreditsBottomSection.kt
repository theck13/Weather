package com.pranshulgg.weather_master_app.feature.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.TouchMinimum
import com.pranshulgg.weather_master_app.core.model.domain.weather.Weather
import com.pranshulgg.weather_master_app.core.model.sources.WeatherSource
import com.pranshulgg.weather_master_app.core.ui.components.Gap

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun CreditsBottomSection(
    onClick: () -> Unit,
    weather: Weather?,
) {
    val source = weather?.location?.source?.displayName ?: WeatherSource.OPEN.displayName

    Gap(
        vertical = 8.dp,
    )

    Text(
        modifier = Modifier
            .clip(
                shape = RoundedCornerShape(
                    size = 8.dp,
                ),
            )
            .clickable(
                onClick = onClick,
            )
            .defaultMinSize(
                minHeight = TouchMinimum,
                minWidth = TouchMinimum,
            )
            .padding(
                all = 16.dp,
            )
            .wrapContentHeight(
                align = Alignment.CenterVertically,
            ),
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge,
        text = "Source: $source",
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
    )

    Gap(
        vertical = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
    )
}
