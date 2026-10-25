package com.heckofanapp.weather.feature.shared.components.blocks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault
import com.heckofanapp.weather.core.model.domain.weather.Weather
import com.heckofanapp.weather.core.prefs.AppPrefsState
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.Symbol
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import com.heckofanapp.weather.core.utils.weather.cache.isWeatherDailyDomainSafe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun CelestialBlock(
    index: Int,
    onClick: (() -> Unit)?,
    state: AppPrefsState,
    type: CelestialType,
    weather: Weather,
) {
    if (isWeatherDailyDomainSafe(weather).not()) return

    val daily = weather.daily[index]
    val drawable = when (type) {
        CelestialType.Moon -> daily.moonPhase.icon
        CelestialType.Sun -> R.drawable.ic_rise_set_sun
    }
    val events = getEvents(
        index = index,
        type = type,
        weather = weather,
    )
    val heightHorizon = 0.40f
    val image = painterResource(drawable)
    val is24Hr = state.timeFormat == "24"
    val formatter: (Long) -> String = {
        if (is24Hr) to24HourTimeString(
            timeMilli = it,
            zoneId = weather.location.timezone,
        ) else to12HourTimeString(
            pattern = "hh:mm a",
            timeMilli = it,
            zoneId = weather.location.timezone,
        )
    }
    val strokeWidth = integerResource(R.integer.stroke_width)
    val timeNow by produceState(
        initialValue = System.currentTimeMillis(),
    ) {
        while (true) {
            delay(
                duration = 1.minutes,
            )
            value = System.currentTimeMillis()
        }
    }
    val timeRise =
        when (type) {
            CelestialType.Moon -> daily.moonrise
            CelestialType.Sun -> daily.sunrise
        }
    val timeSet =
        when (type) {
            CelestialType.Moon -> daily.moonset
            CelestialType.Sun -> daily.sunset
        }
    val viewportSize = integerResource(R.integer.viewport_size)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        enabled = onClick != null,
        onClick = onClick ?: {},
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
    ) {
        Box(
            Modifier
                .aspectRatio(
                    ratio = 1.00f,
                )
                .fillMaxSize(),
        ) {
            Image(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomCenter,
                    )
                    .matchParentSize(),
                alignment = Alignment.BottomCenter,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                contentDescription = "",
                painter = painterResource(R.drawable.il_rise_set_arc),
            )

            Canvas(
                modifier = Modifier
                    .alpha(
                        alpha = if (index == 0) 1.00f else 0.00f,
                    )
                    .fillMaxSize(),
            ) {
                val sizeHeight = size.height
                val sizeIcon = 64.00f
                val sizeStroke = sizeIcon * strokeWidth / viewportSize
                val sizeWidth = size.width

                // Extended past both edges by half icon so icon is completely off at ends.
                val path = Path().apply {
                    moveTo(
                        x = -sizeIcon / 2.00f,
                        y = sizeHeight / 1.45f,
                    )
                    lineTo(
                        x = 0.00f,
                        y = sizeHeight / 1.45f,
                    )
                    cubicTo(
                        sizeWidth * 0.38f, sizeHeight / 2.00f,
                        sizeWidth * 0.38f, sizeHeight / 15.00f,
                        sizeWidth * 0.98f, sizeHeight / 1.40f,
                    )
                    lineTo(
                        x = sizeWidth + sizeIcon / 2.00f,
                        y = sizeHeight / 1.40f,
                    )
                }

                drawPath(
                    color = Color.Transparent,
                    path = path,
                )

                val pathMeasure = PathMeasure().apply {
                    setPath(
                        forceClosed = false,
                        path = path,
                    )
                }
                val position = getPosition(
                    events = events,
                    horizonY = sizeHeight * (1 - heightHorizon),
                    pathMeasure = pathMeasure,
                    sizeIcon = sizeIcon,
                    sizeStroke = sizeStroke,
                    timeNow = timeNow,
                )

                translate(
                    left = position.x - sizeIcon / 2.00f,
                    top = position.y - sizeIcon / 2.00f,
                ) {
                    with(image) {
                        draw(
                            size = Size(
                                height = sizeIcon,
                                width = sizeIcon,
                            ),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.align(
                    alignment = Alignment.TopStart,
                ),
            ) {
                CelestialHeader(
                    type = type,
                )
            }

            Surface(
                modifier = Modifier
                    .align(
                        alignment = Alignment.BottomCenter,
                    )
                    .fillMaxHeight(
                        fraction = heightHorizon,
                    )
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer.copy(
                    alpha = 0.80f,
                ),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.align(
                            alignment = Alignment.Center,
                        ),
                    ) {
                        CelestialTime(
                            icon = R.drawable.ic_arrow_upward_24,
                            text = formatter(timeRise),
                        )

                        CelestialTime(
                            icon = R.drawable.ic_arrow_downward_24,
                            text = formatter(timeSet),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CelestialHeader(
    type: CelestialType,
) {
    val alpha = 0.90f
    val color =  MaterialTheme.colorScheme.onSurface.copy(
        alpha = alpha,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                end = 12.dp,
                start = 12.dp,
                top = SpaceDefault,
            ),
        horizontalArrangement = Arrangement.spacedBy(
            alignment = Alignment.CenterHorizontally,
            space = SpaceDefault / 2,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Symbol(
            color = color,
            icon = type.icon,
        )

        Text(
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = stringResource(type.title),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
internal fun CelestialTime(
    icon: Int,
    text: String,
) {
    Row {
        Symbol(
            color = MaterialTheme.colorScheme.onSurface,
            icon = icon,
            size = SpaceDefault,
        )

        Gap(
            horizontal = SpaceDefault / 2,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
            text = text,
        )
    }
}

internal enum class CelestialType(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
) {
    Moon(
        icon = R.drawable.ic_bedtime_24,
        title = R.string.weather_moon,
    ),
    Sun(
        icon = R.drawable.ic_sunny_24,
        title = R.string.weather_sun,
    ),
}

internal data class CelestialEvent(
    val kind: CelestialEventKind,
    val time: Long,
)

internal enum class CelestialEventKind {
    End,
    Rise,
    Set,
    Start,
}

/**
 * Get sorted start/rise/set/end events for yesterday, today, and tomorrow.
 *
 * Both rise and set of one day need not belong to same pass (moon often sets in morning and rises
 * in evening) so neighboring days are needed to bracket now.  Missing times (zero or negative) are
 * dropped.  When yesterday or tomorrow is not in forecast, today's events are shifted by one solar
 * or lunar day instead.
 *
 * Sun start is dawn and end is dusk.  Moon mimics that by starting before moonrise by same duration
 * as dawn before sunrise, and ending after moonset by same duration as dusk after sunset.
 */
internal fun getEvents(
    index: Int,
    type: CelestialType,
    weather: Weather,
): List<CelestialEvent> {
    val events = mutableListOf<CelestialEvent>()
    val period = when (type) {
        CelestialType.Moon -> (24.hours + 50.minutes).inWholeMilliseconds
        CelestialType.Sun -> 24.hours.inWholeMilliseconds
    }
    val today = (index - 1..index + 1)
    val twilightFallback = 30.minutes.inWholeMilliseconds

    today.forEach { day ->
        val daily = weather.daily[day.coerceIn(0, weather.daily.lastIndex)]
        val offset = when {
            day < 0 -> -period
            day > weather.daily.lastIndex -> period
            else -> 0L
        }
        val timeRise = when (type) {
            CelestialType.Moon -> daily.moonrise
            CelestialType.Sun -> daily.sunrise
        }
        val timeSet = when (type) {
            CelestialType.Moon -> daily.moonset
            CelestialType.Sun -> daily.sunset
        }

        val durationEnd = (daily.dusk - daily.sunset).takeIf { daily.dusk > 0 && it > 0 } ?: twilightFallback
        val durationStart = (daily.sunrise - daily.dawn).takeIf { daily.dawn > 0 && it > 0 } ?: twilightFallback

        if (timeRise > 0) {
            events += CelestialEvent(
                kind = CelestialEventKind.Start,
                time = timeRise - durationStart + offset,
            )
            events += CelestialEvent(
                kind = CelestialEventKind.Rise,
                time = timeRise + offset,
            )
        }

        if (timeSet > 0) {
            events += CelestialEvent(
                kind = CelestialEventKind.Set,
                time = timeSet + offset,
            )
            events += CelestialEvent(
                kind = CelestialEventKind.End,
                time = timeSet + durationEnd + offset,
            )
        }
    }

    return events.sortedBy { it.time }
}

/**
 * Get position of sun/moon icon on arc.
 *
 * Both start and end map to ends of arc, which are past edges so icon is completely off.  Both rise
 * and set map to where top edge of icon touches horizon (top of rise/set panel).  So, icon is fully
 * below horizon until rise and after set.  Icon always moves along arc between consecutive events,
 * and stays off between end and next start.
 */
internal fun getPosition(
    events: List<CelestialEvent>,
    horizonY: Float,
    pathMeasure: PathMeasure,
    sizeIcon: Float,
    sizeStroke: Float,
    timeNow: Long,
): Offset {
    val length = pathMeasure.length
    val steps = 200

    val isAboveHorizon: (Float) -> Boolean = {
        pathMeasure.getPosition(it).y <= horizonY + sizeIcon / 2.00f - sizeStroke
    }

    val getCrossing: (Float, Float) -> Float = { below, above ->
        var distanceAbove = above
        var distanceBelow = below

        repeat(20) {
            val distanceMiddle = (distanceAbove + distanceBelow) / 2.00f

            if (isAboveHorizon(distanceMiddle)) distanceAbove = distanceMiddle else distanceBelow = distanceMiddle
        }

        distanceAbove
    }

    val samples = (0..steps).map { length * it / steps }
    val indexRise = samples.indexOfFirst(isAboveHorizon)
    val indexSet = samples.indexOfLast(isAboveHorizon)

    val riseDistance = when {
        indexRise <= 0 -> 0.00f
        else -> getCrossing(samples[indexRise - 1], samples[indexRise])
    }
    val setDistance = when {
        indexSet < 0 || indexSet == steps -> length
        else -> getCrossing(samples[indexSet + 1], samples[indexSet])
    }

    val distanceOf: (CelestialEventKind) -> Float = {
        when (it) {
            CelestialEventKind.End -> length
            CelestialEventKind.Rise -> riseDistance
            CelestialEventKind.Set -> setDistance
            CelestialEventKind.Start -> 0.00f
        }
    }

    val eventPrevious = events.lastOrNull { it.time <= timeNow }
    val eventNext = events.firstOrNull { it.time > timeNow }

    val distance = when {
        eventPrevious == null -> 0.00f
        eventNext == null || eventPrevious.kind == CelestialEventKind.End -> length
        else -> {
            val fraction = (timeNow - eventPrevious.time).toFloat() / (eventNext.time - eventPrevious.time).coerceAtLeast(1)
            distanceOf(eventPrevious.kind) + (distanceOf(eventNext.kind) - distanceOf(eventPrevious.kind)) * fraction
        }
    }

    return pathMeasure.getPosition(distance)
}
