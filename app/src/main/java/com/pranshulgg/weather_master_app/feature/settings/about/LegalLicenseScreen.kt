package com.pranshulgg.weather_master_app.feature.settings.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold

@Composable
fun LegalLicenseScreen(
    navController: NavController,
) {
    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.about_license),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = rememberScrollState(),
                )
                .padding(
                    paddingValues = paddingValues,
                )
                .padding(
                    horizontal = 16.dp,
                ),
        ) {

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                text = "GNU General Public License v3.0",
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "This application is licensed under the GNU General Public License Version 3 (GPLv3).\n\n" +
                        "You are free to use, study, modify, and distribute this software under the terms of the GPLv3 license.\n\n" +
                        "Any modified versions or redistributed copies must also remain open-source and include the same license.\n\n" +
                        "This software is provided \"as is\", without warranty of any kind, including merchantability or fitness for a particular purpose.",
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Your Rights",
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• Use the software for any purpose\n" +
                        "• Access and modify the source code\n" +
                        "• Share copies of the software\n" +
                        "• Distribute modified versions under GPLv3",
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Conditions",
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• You must include the GPLv3 license when redistributing\n" +
                        "• Modified versions must clearly state changes made\n" +
                        "• Derivative works must also be licensed under GPLv3\n" +
                        "• Source code must remain available",
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Full License",
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                text = buildAnnotatedString {
                    append("You can read the full GNU GPLv3 license at: ")
                    withLink(
                        link = LinkAnnotation.Url("https://www.gnu.org/licenses/gpl-3.0.en.html"),
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) {
                            append("https://www.gnu.org/licenses/gpl-3.0.en.html")
                        }
                    }
                },
            )

            Gap(
                vertical = 10.dp,
            )
        }
    }
}
