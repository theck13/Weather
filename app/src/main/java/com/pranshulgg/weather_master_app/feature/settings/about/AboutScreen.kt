package com.pranshulgg.weather_master_app.feature.settings.about

import androidx.annotation.ArrayRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.WeatherCondition
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.navigation.NavigationRoutes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(
    navController: NavController,
) {
    val appName = stringResource(R.string.app_name)
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val viewModel: AboutScreenViewModel = hiltViewModel()

    val isLoadingNewVersion = viewModel.loading

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(navController)
        },
        title = stringResource(R.string.setting_about_app),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = rememberScrollState()
                    )
                    .padding(
                        paddingValues = paddingValues,
                    ),
            verticalArrangement = Arrangement.spacedBy(
                space = 10.dp,
            ),
        ) {
            val resources = context.resources

            AboutVersion(
                description = "${packageInfo.versionName} (${packageInfo.versionCode})",
                isRefreshing = isLoadingNewVersion,
                onClickIcon = {
                    val messages = resources
                        .getStringArray(viewModel.currentCondition.toAboutArrayRes())
                        .ifEmpty {
                            resources.getStringArray(R.array.array_about_any)
                        }
                    SnackbarManager.show(
                        duration = SnackbarDuration.Indefinite,
                        message = messages.random(),
                    )
                },
                onClickRefresh = {
                    scope.launch {
                        viewModel.isNewVersionAvailable(
                            currentTag = "v${packageInfo.versionName}",
                            onAction = {
                                uriHandler.openUri(
                                    uri = "https://github.com/theck13/Weather/releases/latest",
                                )
                            },
                        )
                    }
                },
            )

            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_license_24,
                            )
                        },
                        onClick = {
                            navController.navigate(
                                route = NavigationRoutes.LICENSE,
                            )
                        },
                        title = stringResource(R.string.about_license),
                    ),
                    SettingTile.ActionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_policy_24,
                            )
                        },
                        onClick = {
                            navController.navigate(
                                route = NavigationRoutes.PRIVACY_POLICY,
                            )
                        },
                        title = stringResource(R.string.about_privacy_policy),
                    ),
                    SettingTile.ActionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_article_24,
                            )
                        },
                        onClick = {
                            navController.navigate(
                                route = NavigationRoutes.TERMS_CONDITIONS,
                            )
                        },
                        title = stringResource(R.string.about_terms_conditions),
                    ),
                )
            )

            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_mail_24,
                            )
                        },
                        onClick = {
                            uriHandler.openUri(
                                uri = "mailto:${BuildConfig.DEVELOPER_EMAIL}?subject=$appName",
                            )
                        },
                        title = stringResource(R.string.about_email),
                        trailing = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_open_in_new_24,
                            )
                        },
                    ),
                    SettingTile.ActionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_list_alt_24,
                            )
                        },
                        onClick = {
                            uriHandler.openUri("https://github.com/theck13/Weather/releases/latest")
                        },
                        title = stringResource(R.string.about_changelog),
                        trailing = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_open_in_new_24,
                            )
                        },
                    ),
                    SettingTile.ActionTile(
                        leading = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_bug_report_24,
                            )
                        },
                        onClick = {
                            uriHandler.openUri("https://github.com/theck13/Weather/issues/new")
                        },
                        title = stringResource(R.string.about_create_issue),
                        trailing = {
                            SettingsTileIcon(
                                icon = R.drawable.ic_open_in_new_24,
                            )
                        },
                    ),
                )
            )

            Gap(
                vertical = 10.dp,
            )
        }
    }
}

@Composable
private fun AboutVersion(
    description: String,
    isRefreshing: Boolean = false,
    onClickIcon: () -> Unit,
    onClickRefresh: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
            ),
        shape = RoundedCornerShape(
            size = ShapeRadius.Large,
        ),
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            headlineContent = {
                Text(
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.app_name),
                )
            },
            leadingContent = {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Transparent,
                    ),
                    onClick = onClickIcon,
                ) {
                    Image(
                        modifier = Modifier.size(
                            size = 40.dp,
                        ),
                        contentDescription = null,
                        painter = painterResource(R.drawable.ic_app),
                    )
                }
            },
            supportingContent = {
                Text(
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    text = description,
                )
            },
            trailingContent = {
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    enabled = isRefreshing.not(),
                    onClick = onClickRefresh,
                    shapes = IconButtonDefaults.shapes(),
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(
                                size = 16.dp,
                            ),
                            color = MaterialTheme.colorScheme.onTertiary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Symbol(
                            color = MaterialTheme.colorScheme.onTertiary,
                            icon = R.drawable.ic_refresh_24,
                        )
                    }
                }
            }
        )
    }
}

@ArrayRes
private fun WeatherCondition?.toAboutArrayRes(): Int {
    return when (this) {
        WeatherCondition.CLEAR_SKY,
        WeatherCondition.MOSTLY_CLEAR,
        WeatherCondition.VERY_HOT -> R.array.array_about_sun

        WeatherCondition.FOG_HAZE,
        WeatherCondition.OVERCAST,
        WeatherCondition.PARTLY_CLOUDY -> R.array.array_about_cloud

        WeatherCondition.HEAVY_RAIN,
        WeatherCondition.LIGHT_RAIN,
        WeatherCondition.RAIN,
        WeatherCondition.THUNDERSTORM -> R.array.array_about_rain

        WeatherCondition.HEAVY_SNOW,
        WeatherCondition.LIGHT_SNOW,
        WeatherCondition.SNOW,
        WeatherCondition.VERY_COLD -> R.array.array_about_snow

        else -> R.array.array_about_any
    }
}
