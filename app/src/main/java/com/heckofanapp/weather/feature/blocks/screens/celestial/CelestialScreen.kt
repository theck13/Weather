package com.heckofanapp.weather.feature.blocks.screens.celestial

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.SpaceDefault
import com.heckofanapp.weather.core.model.astro.MoonPhase
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.components.DialogBasic
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.ui.theme.ShapeRadius
import com.heckofanapp.weather.core.utils.formatters.formatLocalizedNumber
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import com.heckofanapp.weather.core.utils.formatters.toDateString
import com.heckofanapp.weather.core.utils.locale.getCurrentAppLocale
import com.heckofanapp.weather.feature.blocks.BlocksScreenViewModel
import com.heckofanapp.weather.feature.blocks.components.AboutCard
import com.heckofanapp.weather.feature.blocks.components.AboutCardText
import com.heckofanapp.weather.feature.shared.components.blocks.CelestialBlock
import com.heckofanapp.weather.feature.shared.components.blocks.CelestialType
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
@Composable
fun CelestialScreen(
    index: Int,
    locationId: String,
    navController: NavController,
) {
    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }

    val uiState = viewModel.uiState.value
    val weather = uiState.weather

    val daily = uiState.weather?.daily ?: return
    val density = LocalDensity.current
    val preferences = LocalAppPrefs.current

    val is24hr = preferences.timeFormat == "24"
    val isTabletLike = with(density) { LocalWindowInfo.current.containerSize.width.toDp() } > 600.dp

    val date = toDateString(
        timeMilli = daily[index].time,
        zoneId = weather.location.timezone,
    )
    val dawnFormatted =
        if (is24hr) {
            to24HourTimeString(
                timeMilli = daily[index].dawn,
                zoneId = weather.location.timezone,
            )
        } else {
            to12HourTimeString(
                pattern = "hh:mm a",
                timeMilli = daily[index].dawn,
                zoneId = weather.location.timezone,
            )
        }
    val duskFormatted =
        if (is24hr) {
            to24HourTimeString(
                timeMilli = daily[index].dusk,
                zoneId = weather.location.timezone,
            )
        } else {
            to12HourTimeString(
                pattern = "hh:mm a",
                timeMilli = daily[index].dusk,
                zoneId = weather.location.timezone,
            )
        }

    val dayLength = daily[index].sunset.minus(daily[index].sunrise)
    val dayLengthHours = TimeUnit.MILLISECONDS.toHours(dayLength)
    val dayLengthMinutes = TimeUnit.MILLISECONDS.toMinutes(dayLength) % 60

    val moonIlluminationPercent = formatLocalizedNumber(
        decimalPlaces = 1,
        locale = getCurrentAppLocale(),
        number = daily[index].moonIllumination,
    )
    val moonPhaseDaysRemaining = daily[index].moonPhaseDaysRemaining
    val moonPhaseDaysText = pluralStringResource(
        R.plurals.phase_days,
        moonPhaseDaysRemaining,
        moonPhaseDaysRemaining,
    )

    var showMoonPhaseDialog by remember { mutableStateOf(false) }

    TopBarScaffold(
        actions = {
            Text(
                modifier = Modifier.padding(
                    end = SpaceDefault,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                text = date,
            )
        },
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.weather_sun_moon),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    state = rememberScrollState(),
                )
                .padding(
                    paddingValues = paddingValues,
                )
                .padding(
                    bottom = SpaceDefault,
                    top = 2.dp,
                ),
        ) {
            if (isTabletLike) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = SpaceDefault,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = SpaceDefault,
                    ),
                ) {
                    Surface(
                        modifier = Modifier.weight(
                            weight = 1.00f,
                        ),
                        color = MaterialTheme.colorScheme.surfaceBright,
                        shape = MaterialTheme.shapes.extraLarge,
                        shadowElevation = ShadowElevation.level2,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                    .padding(
                                    all = SpaceDefault,
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(
                                    space = SpaceDefault / 2,
                                ),
                            ) {
                                TextHeader(
                                    header = stringResource(R.string.text_dawn),
                                    text = dawnFormatted,
                                )

                                TextHeader(
                                    header = stringResource(R.string.text_dusk),
                                    text = duskFormatted,
                                )

                                TextHeader(
                                    header = stringResource(R.string.text_day_length),
                                    text = "$dayLengthHours:$dayLengthMinutes",
                                )
                            }

                            Box(
                                modifier = Modifier.size(
                                    size = 160.dp,
                                ),
                            ) {
                                CelestialBlock(
                                    index = index,
                                    onClick = null,
                                    state = preferences,
                                    type = CelestialType.Sun,
                                    weather = weather,
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(
                            weight = 1.00f,
                        ),
                        color = MaterialTheme.colorScheme.surfaceBright,
                        shape = MaterialTheme.shapes.extraLarge,
                        shadowElevation = ShadowElevation.level2,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    all = SpaceDefault,
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(
                                modifier = Modifier.weight(
                                    weight = 1.00f,
                                ),
                                verticalArrangement = Arrangement.spacedBy(
                                    space = SpaceDefault / 2,
                                ),
                            ) {
                                TextHeader(
                                    header = stringResource(R.string.moon_phase),
                                    onClick = {
                                        showMoonPhaseDialog = true
                                    },
                                    text = stringResource(daily[index].moonPhase.displayName),
                                )

                                TextHeader(
                                    header = stringResource(R.string.moon_phase_prevailing),
                                    text = moonPhaseDaysText,
                                )

                                TextHeader(
                                    header = stringResource(R.string.moon_phase_proportion),
                                    text = stringResource(R.string.percentage, moonIlluminationPercent),
                                )
                            }

                            Box(
                                modifier = Modifier.size(
                                    size = 160.dp,
                                ),
                            ) {
                                CelestialBlock(
                                    index = index,
                                    onClick = null,
                                    state = preferences,
                                    type = CelestialType.Moon,
                                    weather = weather,
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.padding(
                        horizontal = SpaceDefault,
                    ),
                    color = MaterialTheme.colorScheme.surfaceBright,
                    shape = MaterialTheme.shapes.extraLarge,
                    shadowElevation = ShadowElevation.level2,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                all = SpaceDefault,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                space = SpaceDefault / 2,
                            ),
                        ) {
                            TextHeader(
                                header = stringResource(R.string.text_dawn),
                                text = dawnFormatted,
                            )

                            TextHeader(
                                header = stringResource(R.string.text_dusk),
                                text = duskFormatted,
                            )

                            TextHeader(
                                header = stringResource(R.string.text_day_length),
                                text = "$dayLengthHours:$dayLengthMinutes",
                            )
                        }

                        Box(
                            modifier = Modifier.size(
                                size = 160.dp,
                            ),
                        ) {
                            CelestialBlock(
                                index = index,
                                onClick = null,
                                state = preferences,
                                type = CelestialType.Sun,
                                weather = weather,
                            )
                        }
                    }
                }

                Gap(
                    vertical = SpaceDefault,
                )

                Surface(
                    modifier = Modifier.padding(
                        horizontal = SpaceDefault,
                    ),
                    color = MaterialTheme.colorScheme.surfaceBright,
                    shape = MaterialTheme.shapes.extraLarge,
                    shadowElevation = ShadowElevation.level2,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                all = SpaceDefault,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            modifier = Modifier.weight(
                                weight = 1.00f,
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                space = SpaceDefault / 2,
                            ),
                        ) {
                            TextHeader(
                                header = stringResource(R.string.moon_phase),
                                onClick = {
                                    showMoonPhaseDialog = true
                                },
                                text = stringResource(daily[index].moonPhase.displayName),
                            )

                            TextHeader(
                                header = stringResource(R.string.moon_phase_prevailing),
                                text = moonPhaseDaysText,
                            )

                            TextHeader(
                                header = stringResource(R.string.moon_phase_proportion),
                                text = stringResource(R.string.percentage, moonIlluminationPercent),
                            )
                        }

                        Box(
                            modifier = Modifier.size(
                                size = 160.dp,
                            ),
                        ) {
                            CelestialBlock(
                                index = index,
                                onClick = null,
                                state = preferences,
                                type = CelestialType.Moon,
                                weather = weather,
                            )
                        }
                    }
                }
            }

            Gap(
                vertical = SpaceDefault,
            )

            AboutCard {
                AboutCardText(
                    text = stringResource(R.string.weather_about_sun_moon_rise_set),
                )

                AboutCardText(
                    text = stringResource(R.string.weather_about_dawn_dusk),
                )
            }

            Gap(
                vertical = SpaceDefault,
            )
        }

        MoonPhaseDialog(
            currentPhase = daily[index].moonPhase,
            onDismiss = {
                showMoonPhaseDialog = false
            },
            show = showMoonPhaseDialog,
        )
    }
}

@Composable
private fun MoonPhaseDialog(
    currentPhase: MoonPhase,
    onDismiss: () -> Unit,
    show: Boolean,
) {
    DialogBasic(
        isDefaultActions = false,
        onDismiss = onDismiss,
        show = show,
        textTitle = stringResource(R.string.moon_phases),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .weight(
                        weight = 1.00f,
                    )
                    .verticalScroll(
                        state = rememberScrollState(),
                    )
                    .padding(
                        horizontal = 24.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                ),
            ) {
                MoonPhase.entries.forEach { phase ->
                    val isCurrent = phase == currentPhase

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = SpaceDefault / 2,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(
                                    weight = 1.00f,
                                )
                        ) {
                            Text(
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isCurrent) FontWeight.W700 else FontWeight.W400,
                                style = MaterialTheme.typography.titleMedium,
                                text = stringResource(phase.displayName),
                            )

                            Text(
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isCurrent) FontWeight.W700 else FontWeight.W400,
                                style = MaterialTheme.typography.bodyMedium,
                                text = stringResource(phase.description),
                            )
                        }

                        Image(
                            modifier = Modifier
                                .size(
                                    size = SpaceDefault * 4,
                                ),
                            colorFilter = ColorFilter.colorMatrix(
                                ColorMatrix().apply {
                                    setToSaturation(
                                        sat = if (isCurrent) 1.00f else 0.00f,
                                    )
                                }
                            ),
                            contentDescription = "",
                            painter = painterResource(phase.image),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 24.dp,
                        end = 24.dp,
                        start = 24.dp,
                        top = SpaceDefault,
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onDismiss,
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = stringResource(R.string.action_ok),
                    )
                }
            }
        }
    }
}

@Composable
private fun TextHeader(
    header: String,
    onClick: (() -> Unit)? = null,
    text: String,
) {
    val modifier = onClick?.let {
        Modifier
            .clip(
                shape = RoundedCornerShape(
                    size = ShapeRadius.ExtraSmall,
                )
            )
            .clickable(
                onClick = {
                    onClick.invoke()
                }
            )
    } ?: Modifier

    Column(
        modifier = modifier,
    ) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.W700,
            text = header,
        )

        Text(
            color = MaterialTheme.colorScheme.onSurface,
            text = text,
        )
    }
}
