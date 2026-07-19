package com.heckofanapp.weather.feature.settings.sources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon
import com.heckofanapp.weather.core.ui.components.TopBarScaffold

@Composable
fun WeatherSourcesScreen(
    navController: NavController,
) {
    val uriHandler = LocalUriHandler.current

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(navController)
        },
        title = stringResource(R.string.weather_sources),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = rememberScrollState(),
                )
                .padding(
                    paddingValues = paddingValues,
                ),
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.setting_weather_sources_info),
            )

            Gap(
                vertical = 10.dp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.settings_source_request_title),
                )

                Button(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                    ),
                    onClick = {
                        uriHandler.openUri("https://github.com/theck13/Weather/issues/new?template=new_source.yaml")
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(
                        text = stringResource(R.string.action_request),
                    )
                }
            }

            Gap(
                vertical = 10.dp,
            )

            WeatherSource.entries.forEach {
                SettingSection(
                    title = it.displayName,
                    tiles = listOf(
                        SettingTile.ActionTile(
                            description = it.displayLink,
                            onClick = {
                                uriHandler.openUri(it.displayLink)
                            },
                            title = it.fullName,
                            trailing = {
                                SettingsTileIcon(
                                    icon = R.drawable.ic_open_in_new_24,
                                )
                            },
                        ),
                    ),
                )

                Gap(
                    vertical = 10.dp,
                )
            }

            Gap(
                vertical = 10.dp,
            )
        }
    }
}
