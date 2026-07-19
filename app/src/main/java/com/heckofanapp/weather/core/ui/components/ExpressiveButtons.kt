package com.heckofanapp.weather.core.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun ExpressiveButton(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabled: Boolean = false,
    icon: Int? = null,
    onClick: () -> Unit,
    size: Dp = ButtonDefaults.MinHeight,
    text: String,
) {
    val disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
        alpha = 0.38f,
    )

    Button(
        modifier = modifier.heightIn(
            min = size,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        contentPadding = ButtonDefaults.contentPaddingFor(
            buttonHeight = size,
            hasStartIcon = icon != null,
        ),
        enabled = disabled.not(),
        onClick = {
            onClick()
        },
        shapes = ButtonDefaults.shapes(),
    ) {
        if (icon != null) {
            Symbol(
                color = if (disabled) disabledContentColor else contentColor,
                icon = icon,
                size = ButtonDefaults.iconSizeFor(
                    buttonHeight = size,
                ),
            )

            Gap(
                horizontal = ButtonDefaults.iconSpacingFor(
                    buttonHeight = size,
                ),
            )
        }
        Text(
            color = if (disabled) disabledContentColor else contentColor,
            style = ButtonDefaults.textStyleFor(
                buttonHeight = size,
            ),
            text = text,
        )
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun ExpressiveButtonOutlined(
    modifier: Modifier = Modifier,
    disabled: Boolean = false,
    icon: Int? = null,
    onClick: () -> Unit,
    size: Dp = ButtonDefaults.MinHeight,
    text: String,
) {
    OutlinedButton(
        modifier = modifier.heightIn(
            min = size,
        ),
        contentPadding = ButtonDefaults.contentPaddingFor(
            buttonHeight = size,
            hasStartIcon = icon != null,
        ),
        enabled = disabled.not(),
        onClick = {
            onClick()
        },
        shapes = ButtonDefaults.shapes(),
    ) {
        if (icon != null) {
            Symbol(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                icon = icon,
                size = ButtonDefaults.iconSizeFor(
                    buttonHeight = size,
                ),
            )

            Gap(
                horizontal = ButtonDefaults.iconSpacingFor(
                    buttonHeight = size,
                ),
            )
        }
        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = ButtonDefaults.textStyleFor(
                buttonHeight = size,
            ),
            text = text,
        )
    }
}

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun ExpressiveButtonTonal(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabled: Boolean = false,
    icon: Int? = null,
    onClick: () -> Unit,
    size: Dp = ButtonDefaults.MinHeight,
    text: String,
) {
    FilledTonalButton(
        modifier = modifier.heightIn(size),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = ButtonDefaults.contentPaddingFor(
            buttonHeight = size,
            hasStartIcon = icon != null,
        ),
        enabled = disabled.not(),
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes(),
    ) {
        if (icon != null) {
            Symbol(
                color = contentColor,
                icon = icon,
                size = ButtonDefaults.iconSizeFor(
                    buttonHeight = size,
                ),
            )

            Gap(
                horizontal = ButtonDefaults.iconSpacingFor(
                    buttonHeight = size,
                ),
            )
        }

        Text(
            color = contentColor,
            style = ButtonDefaults.textStyleFor(
                buttonHeight = size,
            ),
            text = text,
        )
    }
}
