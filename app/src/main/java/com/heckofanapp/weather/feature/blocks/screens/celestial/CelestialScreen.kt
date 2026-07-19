package com.heckofanapp.weather.feature.blocks.screens.celestial

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.astro.MoonPhase
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.components.DialogBasic
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.ui.theme.ShadowElevation
import com.heckofanapp.weather.core.utils.formatters.to12HourTimeString
import com.heckofanapp.weather.core.utils.formatters.to24HourTimeString
import com.heckofanapp.weather.core.utils.formatters.toDateString
import com.heckofanapp.weather.feature.blocks.BlocksScreenViewModel
import com.heckofanapp.weather.feature.blocks.components.AboutCard
import com.heckofanapp.weather.feature.blocks.components.AboutCardText
import com.heckofanapp.weather.feature.shared.components.blocks.CelestialBlock
import com.heckofanapp.weather.feature.shared.components.blocks.CelestialType
import java.util.concurrent.TimeUnit

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

    var showMoonPhaseDialog by remember { mutableStateOf(false) }

    TopBarScaffold(
        actions = {
            Text(
                modifier = Modifier.padding(
                    end = 16.dp,
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
                    bottom = 16.dp,
                    top = 2.dp,
                ),
        ) {
            if (isTabletLike) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 14.dp,
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
                                    all = 16.dp,
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 6.dp,
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
                                    all = 16.dp,
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(
                                modifier = Modifier.weight(
                                    weight = 1.00f,
                                ),
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 6.dp,
                                ),
                            ) {
                                TextHeader(
                                    header = stringResource(R.string.moon_phase),
                                    text = stringResource(daily[index].moonPhase.displayName),
                                )

                                Gap(
                                    vertical = 4.dp,
                                )

                                Image(
                                    modifier = Modifier
                                        .clip(
                                            shape = CircleShape,
                                        )
                                        .clickable {
                                            showMoonPhaseDialog = true
                                        }
                                        .size(
                                            size = 96.dp,
                                        ),
                                    contentDescription = "",
                                    painter = painterResource(daily[index].moonPhase.image),
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
                        horizontal = 16.dp,
                    ),
                    color = MaterialTheme.colorScheme.surfaceBright,
                    shape = MaterialTheme.shapes.extraLarge,
                    shadowElevation = ShadowElevation.level2,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                all = 16.dp,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                space = 6.dp,
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
                    vertical = 14.dp,
                )

                Surface(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    color = MaterialTheme.colorScheme.surfaceBright,
                    shape = MaterialTheme.shapes.extraLarge,
                    shadowElevation = ShadowElevation.level2,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                all = 16.dp,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            modifier = Modifier.weight(
                                weight = 1.00f,
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                space = 6.dp,
                            ),
                        ) {
                            TextHeader(
                                header = stringResource(R.string.moon_phase),
                                text = stringResource(daily[index].moonPhase.displayName),
                            )

                            Gap(
                                vertical = 4.dp,
                            )

                            Image(
                                modifier = Modifier
                                    .clip(
                                        shape = CircleShape,
                                    )
                                    .clickable {
                                        showMoonPhaseDialog = true
                                    }
                                    .size(
                                        size = 96.dp,
                                    ),
                                contentDescription = "",
                                painter = painterResource(daily[index].moonPhase.image),
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
                vertical = 14.dp,
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
                vertical = 10.dp,
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

                    Column {
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
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 24.dp,
                        end = 24.dp,
                        start = 24.dp,
                        top = 16.dp,
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
    text: String,
) {
    Column {
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
