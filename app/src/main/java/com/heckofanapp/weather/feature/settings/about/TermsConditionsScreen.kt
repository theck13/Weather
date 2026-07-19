package com.heckofanapp.weather.feature.settings.about

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
import com.heckofanapp.weather.BuildConfig
import com.heckofanapp.weather.R
import com.heckofanapp.weather.core.ui.components.Gap
import com.heckofanapp.weather.core.ui.components.NavigateBackButton
import com.heckofanapp.weather.core.ui.components.TopBarScaffold
import com.heckofanapp.weather.core.ui.theme.weatherMasterTitleFont

@Composable
fun TermsConditionsScreen(
    navController: NavController,
) {
    val appName = stringResource(R.string.app_name)

    TopBarScaffold(
        navigationIcon = {
            NavigateBackButton(
                navController = navController,
            )
        },
        title = stringResource(R.string.about_terms_conditions),
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
                text = buildAnnotatedString {
                    append("These Terms & Conditions apply to the ")

                    withStyle(
                        style = SpanStyle(
                            fontFamily = weatherMasterTitleFont,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(
                            text = appName,
                        )
                    }

                    append(" app (the \"Application\") for mobile devices.\n\n")

                    append("This was created by ")

                    withStyle(
                        style = SpanStyle(
                            fontFamily = weatherMasterTitleFont,
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append(BuildConfig.DEVELOPER_NAME)
                    }

                    append(" as an open-source application.  By using the Application, you agree to the following:")
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Use of the Application",
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    append("• The Application is provided ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("as-is")
                    }

                    append(" ,free of charge, and without any guarantees of reliability, availability, or accuracy.")
                },
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• You may use, modify, and distribute the Application in accordance with its open-source license",
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    append("• You may ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("not")
                    }

                    append(" misrepresent the origin of the Application or use its name/trademarks without permission.")
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Data & Privacy",
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    append("• The Application does ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("not collect, store, or share")
                    }

                    append(" any personal information.")
                },
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    append("• The only permission requested is ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("location access")
                    }

                    append(" ,which is optional and used solely within the Application to provide weather information.  This data never leaves your device.")
                },
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• For more details, please see the Privacy Policy.",
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Liability",
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = buildAnnotatedString {
                    append("• The Service Provider (${BuildConfig.DEVELOPER_NAME}) is ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                        ),
                    ) {
                        append("not liable")
                    }

                    append(" for any direct or indirect damages, losses, or issues that may arise from using the Application.")
                },
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• You are responsible for ensuring your device is compatible and has sufficient internet and battery to use the Application.",
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Updates & Availability",
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• The Application may be updated from time to time.",
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• There is no guarantee that the Application will always remain available, functional, or supported on all operating system versions.",
            )

            Gap(
                vertical = 6.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "• The Service Provider may discontinue the Application at any time without prior notice.",
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                ),
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                text = "Changes to These Terms",
            )

            Gap(
                vertical = 8.dp,
            )

            Text(
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                text = "These Terms & Conditions may be updated in the future.  Updates will be posted in the project repository or within the Application.  Continued use of the Application means you accept any revised terms.",
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
                    append("If you have any questions about these Terms & Conditions, please contact ")

                    withLink(
                        link = LinkAnnotation.Url(
                            url = "mailto:${BuildConfig.DEVELOPER_EMAIL}?subject=$appName Terms %26 Conditions"
                        ),
                    ) {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) {
                            append(
                                text = BuildConfig.DEVELOPER_EMAIL,
                            )
                        }
                    }

                    append(" for any direct or indirect damages, losses, or issues that may arise from using the Application.")
                },
            )

            Gap(
                vertical = 10.dp,
            )
        }
    }
}
