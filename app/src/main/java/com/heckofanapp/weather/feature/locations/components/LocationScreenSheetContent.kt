package com.heckofanapp.weather.feature.locations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.components.SettingSection
import com.heckofanapp.weather.core.ui.components.SettingTile
import com.heckofanapp.weather.core.ui.components.SettingsTileIcon

@Composable
fun LocationScreenSheetContent(
    locationName: String,
    onDelete: () -> Unit,
    onSetAsDefault: () -> Unit,
    onReorderLocations: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            bottom = 16.dp,
            top = 8.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
        ),
    ) {
        SettingSection(
            isModalOption = true,
            tiles = listOf(
                SettingTile.ActionTile(
                    leading = {
                        SettingsTileIcon(
                            icon = R.drawable.ic_reorder_24,
                        )
                    },
                    onClick = {
                        onReorderLocations()
                    },
                    title = stringResource(R.string.action_reorder_locations),
                ),
            ),
        )

        SettingSection(
            isModalOption = true,
            title = locationName,
            tiles = listOf(
                SettingTile.ActionTile(
                    leading = {
                        SettingsTileIcon(
                            icon = R.drawable.ic_delete_24,
                        )
                    },
                    onClick = {
                        onDelete()
                    },
                    title = stringResource(R.string.action_delete_location),
                ),
                SettingTile.ActionTile(
                    leading = {
                        SettingsTileIcon(
                            icon = R.drawable.ic_home_pin_24,
                        )
                    },
                    onClick = {
                        onSetAsDefault()
                    },
                    title = stringResource(R.string.action_set_default),
                ),
            ),
        )
    }
}