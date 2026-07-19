package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun NavigateBackButton(
    navController: NavController,
) {
    Tooltip(
        preferredPosition = TooltipAnchorPosition.Below,
        spacing = 10.dp,
        tooltipText = stringResource(R.string.navigate_back),
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            shapes = IconButtonDefaults.shapes(),
        ) {
            Symbol(
                color = MaterialTheme.colorScheme.onSurface,
                description = stringResource(R.string.arrow_back),
                icon = R.drawable.ic_arrow_back_24,
            )
        }
    }
}
