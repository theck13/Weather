package com.heckofanapp.weather.feature.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.model.weather.toFroggy

@Composable
fun FroggyContainer(
    weather: Weather,
) {
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp
            ),
        contentDescription = "",
        painter = painterResource(
            weather.current.weatherCondition.toFroggy(
                daily = weather.daily.firstOrNull(),
                targetTimeMilli = weather.current.time,
            )
        ),
    )
}
