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
import com.pranshulgg.weather_master_app.BuildConfig
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.NavigateBackButton
import com.pranshulgg.weather_master_app.core.ui.components.TopBarScaffold

@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
) {
    val appName = stringResource(R.string.app_name)

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.about_privacy_policy),
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
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "Weather is an open-source application.\n\n" +
                        "We respect your privacy.  The application itself does not:",
            )
            
            Gap(
                vertical = 6.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• Collect, store, or share personal information\n" +
                        "• Track your IP address, usage data, or device identifiers\n" +
                        "• Send personal data to us directly",
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Third-Party Services & APIs",
            )
            
            Gap(
                vertical = 8.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "Weather relies on external weather and geolocation services to provide forecasts and location-based functionality.  These third-party services may process certain technical information, such as:",
            )
            
            Gap(
                vertical = 6.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• IP address\n" +
                        "• Approximate/Precise location data\n" +
                        "• Device or network-related information required for API requests\n\n" +
                        "This data processing is handled by the respective service providers under their own privacy policies.  Weather itself does not control or store this data.",
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Location Permission",
            )
            
            Gap(
                vertical = 8.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• The app may request access to your device’s location in order to display local weather information\n" +
                        "• Granting location permission is optional\n" +
                        "• Your precise location is used locally within the app to fetch weather data from external APIs and is not stored by Weather",
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Children",
            )
            
            Gap(
                vertical = 8.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "This application is not directed toward children under 13 and does not knowingly collect personal information.",
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Changes",
            )
            
            Gap(
                vertical = 8.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "This Privacy Policy may be updated from time to time.  Continued use of the app after changes become effective means you accept the revised policy.",
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Contact",
            )
            
            Gap(
                vertical = 8.dp,
            )
            
            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    append("If you have any questions about privacy, please contact ")

                    withLink(
                        link =  LinkAnnotation.Url(
                            url = "mailto:${BuildConfig.DEVELOPER_EMAIL}?subject=$appName Privacy Policy"
                        ),
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) {
                            append(BuildConfig.DEVELOPER_EMAIL)
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
