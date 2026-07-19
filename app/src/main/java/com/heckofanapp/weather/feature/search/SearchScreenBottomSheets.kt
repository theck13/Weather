package com.heckofanapp.weather.feature.search

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.model.sources.SearchSource
import com.heckofanapp.weather.core.prefs.AppPrefsState
import com.heckofanapp.weather.core.ui.components.ActionBottomSheet
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.Symbol

object SearchScreenBottomSheets {
    @OptIn(
        ExperimentalMaterial3Api::class,
    )
    @Composable
    fun SearchSourcePickerSheet(
        prefs: AppPrefsState,
        sheetState: SheetState,
        uiState: SearchUiState,
        viewModel: SearchScreenViewModel,
    ) {
        if (uiState.isSearchSourcePickerSheetOpen) {
            val sources = SearchSource.entries

            var selectedSource by remember { mutableStateOf(prefs.searchSource) }

            ActionBottomSheet(
                cancelText = stringResource(R.string.action_cancel),
                confirmText = stringResource(R.string.action_save),
                onCancel = viewModel::hideSearchSourcePickerSheet,
                onConfirm = {
                    viewModel.updateSource(
                        prefs = prefs,
                        source = selectedSource,
                    )
                    viewModel.removeResults()
                },
                sheetState = sheetState,
            ) {
                SettingSection(
                    title = stringResource(R.string.search_source),
                    tiles = sources.map { source ->
                        val isSelected = selectedSource == source

                        SettingTile.ActionTile(
                            onClick = {
                                selectedSource = source
                            },
                            selected = isSelected,
                            title = source.displayName,
                            trailing = {
                                if (isSelected) Symbol(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    icon = R.drawable.ic_check_24,
                                )
                            },
                        )
                    },
                )
            }
        }
    }
}