package com.heckofanapp.weather.feature.search

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.domain.location.Location
import com.heckofanapp.weather.core.model.sources.SearchSource
import com.heckofanapp.weather.core.model.sources.WeatherSource
import com.heckofanapp.weather.core.prefs.LocalAppPrefs
import com.heckofanapp.weather.core.ui.components.EmptyContainerPlaceholder
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.LoadingScreenPlaceholder
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.Symbol
import com.heckofanapp.weather.core.ui.components.Tooltip
import com.heckofanapp.weather.feature.shared.ui.SharedBottomSheet

data class SearchUiState(
    val query: String = "",
    val source: SearchSource = SearchSource.OPEN_METEO,
    val isSearchSourcePickerSheetOpen: Boolean = false,
    val isWeatherSourcesForLocationSheetOpen: Boolean = false
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun SearchScreen(
    navController: NavController,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val preferences = LocalAppPrefs.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val viewModel: SearchScreenViewModel = hiltViewModel()

    val loading = viewModel.loading
    val results = viewModel.results
    val uiState by viewModel.uiState
    val query = uiState.query

    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var selectedLocationId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.updateSource(preferences.searchSource, preferences)
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(
            connection = scrollBehavior.nestedScrollConnection,
        ),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                actions = {
                    Tooltip(
                        preferredPosition = TooltipAnchorPosition.Below,
                        spacing = 10.dp,
                        tooltipText = stringResource(R.string.search),
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.search(query.trimEnd())
                                focusManager.clearFocus()
                            },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Symbol(
                                color = MaterialTheme.colorScheme.onSurface,
                                icon = R.drawable.ic_search_24,
                            )
                        }
                    }

                    Tooltip(
                        preferredPosition = TooltipAnchorPosition.Below,
                        spacing = 10.dp,
                        tooltipText = stringResource(R.string.source),
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.showSearchSourcePickerSheet()
                            },
                            shapes = IconButtonDefaults.shapes(),
                        ) {
                            Symbol(
                                icon = R.drawable.ic_settings_24,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                navigationIcon = {
                    NavigateBackButton(navController)
                },
                scrollBehavior = scrollBehavior,
                title = {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(
                                focusRequester = focusRequester,
                            ),
                        colors = TextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.search(query.trimEnd())
                                focusManager.clearFocus()
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search,
                        ),
                        onValueChange = viewModel::updateQuery,
                        placeholder = {
                            Text(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.50f,
                                ),
                                text = stringResource(R.string.search),
                            )
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                        ),
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        viewModel.updateQuery("")
                                    },
                                ) {
                                    Symbol(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        icon = R.drawable.ic_close_24,
                                    )
                                }
                            }
                        },
                        value = query,
                    )
                },
            )
        },
    ) { paddingValues ->
        BoxWithConstraints {
            if (loading) {
                LoadingScreenPlaceholder(
                    fraction = 0.40f,
                    paddingValues = paddingValues,
                )
            }

            if (results.isEmpty() && loading.not()) {
                EmptyContainerPlaceholder(
                    description = stringResource(R.string.search_empty_state_secondary),
                    icon = R.drawable.ic_search_24,
                    isLandscape = maxWidth > maxHeight,
                    paddingValues = paddingValues,
                    text = stringResource(R.string.search_empty_state_title),
                )
            }

            if (results.isNotEmpty() && loading.not()) {
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
                        style = MaterialTheme.typography.bodyMedium,
                        text = stringResource(
                            R.string.search_results_provided,
                            preferences.searchSource.displayName,
                        ),
                    )
                    Gap(
                        vertical = 8.dp,
                    )

                    SettingSection(
                        tiles = results.map {
                            SettingTile.ActionTile(
                                description = "${if (it.state.isNotEmpty()) "${it.state}, " else ""}${it.country}",
                                onClick = {
                                    if (selectedLocationId.isNotEmpty()) return@ActionTile
                                    selectedLocation = it
                                    viewModel.showWeatherSourcesForLocationSheet()
                                },
                                title = it.name,
                                trailing = {
                                    if (selectedLocationId == it.id) {
                                        LoadingIndicator()
                                    }
                                },
                            )
                        }
                    )

                    Gap(
                        vertical = 10.dp,
                    )
                }
            }
        }
    }

    SearchScreenBottomSheets.SearchSourcePickerSheet(
        prefs = preferences,
        sheetState = sheetState,
        uiState = uiState,
        viewModel = viewModel,
    )

    SharedBottomSheet.WeatherSourcesForLocationSheet(
        countryCode = selectedLocation?.countryCode,
        onDismiss = {
            viewModel.hideWeatherSourcesForLocationSheet()
        },
        onSave = {
            viewModel.hideWeatherSourcesForLocationSheet()
            viewModel.saveLocation(
                location = selectedLocation,
                onBack = {
                    navController.popBackStack()
                },
                onReset = {
                    selectedLocationId = ""
                },
                weatherSource = it,
            )
            selectedLocationId = selectedLocation?.id ?: ""
        },
        selectedSource = selectedLocation?.source ?: WeatherSource.OPEN,
        show = uiState.isWeatherSourcesForLocationSheetOpen,
        sheetState = sheetState,
    )
}
