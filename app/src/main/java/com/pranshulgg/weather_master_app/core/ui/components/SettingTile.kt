package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.core.ui.components.tiles.ActionTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.CategoryTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOptionTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogSliderTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogTextFieldTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.SingleSwitchTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.SwitchTile
import com.pranshulgg.weather_master_app.core.ui.components.tiles.TextTile
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius

sealed class SettingTile {
    abstract val title: String
    abstract val description: String?
    open val enabled: Boolean = true

    data class TextTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val descriptionMaxLines: Int = Int.MAX_VALUE,
        val leading: (@Composable (() -> Unit))? = null,
    ) : SettingTile()

    data class ActionTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val colorDesc: Color = Color.Unspecified,
        val danger: Boolean = false,
        val leading: (@Composable (() -> Unit))? = null,
        val onClick: () -> Unit,
        val selected: Boolean = false,
        val trailing: (@Composable (() -> Unit))? = null,
    ) : SettingTile()

    data class CategoryTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val color: Color,
        val leading: Int,
        val onClick: () -> Unit,
        val onColor: Color,
    ) : SettingTile()

    data class DialogOptionTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val dialogTitle: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val onOptionChange: (String) -> Unit = {},
        val onOptionSelected: (String) -> Unit,
        val options: List<DialogOption<String>>,
        val selectedOption: String?,
    ) : SettingTile()

    data class DialogSliderTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val dialogTitle: String,
        val initialValue: Float = 0.5f,
        val isDescriptionAsValue: Boolean = false,
        val labelFormatter: (Float) -> String = { it.toString() },
        val leading: (@Composable (() -> Unit))? = null,
        val onValueChange: (Float) -> Unit = {},
        val onValueSubmitted: (Float) -> Unit,
        val steps: Int = 0,
        val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    ) : SettingTile()

    data class DialogTextFieldTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val initialText: String = "",
        val leading: (@Composable (() -> Unit))? = null,
        val onTextSubmitted: (String) -> Unit,
        val placeholder: String,
        val placeholderTextField: String,
    ) : SettingTile()

    data class SingleSwitchTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val checked: Boolean,
        val leading: (@Composable (() -> Unit))? = null,
        val onCheckedChange: (Boolean) -> Unit,
    ) : SettingTile()

    data class SwitchTile(
        override val description: String? = null,
        override val enabled: Boolean = true,
        override val title: String,
        val checked: Boolean,
        val leading: (@Composable (() -> Unit))? = null,
        val onCheckedChange: (Boolean) -> Unit,
    ) : SettingTile()
}

@Composable
fun SettingSection(
    isModalOption: Boolean = false,
    noPadding: Boolean = false,
    primarySwitch: Boolean = false,
    tiles: List<SettingTile?>,
    title: String? = null,
) {
    val background =
        if (isModalOption) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        }

    Column(
        modifier = Modifier.padding(
            horizontal =
                if (noPadding) {
                    0.dp
                } else {
                    16.dp
                },
        ),
        verticalArrangement = Arrangement.spacedBy(
            space = 2.dp,
        ),
    ) {
        title?.let {
            Text(
                modifier = Modifier.padding(
                    bottom = 5.dp,
                    start = 3.dp,
                    top = 5.dp,
                ),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                text = it,
            )
        }

        val nonNullTiles = tiles.filterNotNull()

        nonNullTiles.forEachIndexed { index, tile ->
            val isOnly = nonNullTiles.size == 1
            val isFirst = nonNullTiles.size > 1 && index == 0
            val isLast = nonNullTiles.size > 1 && index == nonNullTiles.lastIndex
            val primarySwitch = primarySwitch

            val shape = when {
                isFirst -> RoundedCornerShape(
                    bottomEnd = ShapeRadius.ExtraSmall,
                    bottomStart = ShapeRadius.ExtraSmall,
                    topEnd = ShapeRadius.Large,
                    topStart = ShapeRadius.Large,
                )

                isLast -> RoundedCornerShape(
                    bottomEnd = ShapeRadius.Large,
                    bottomStart = ShapeRadius.Large,
                    topEnd = ShapeRadius.ExtraSmall,
                    topStart = ShapeRadius.ExtraSmall,
                )

                isOnly -> RoundedCornerShape(
                    size = ShapeRadius.Large,
                )

                primarySwitch -> RoundedCornerShape(
                    size = ShapeRadius.Full,
                )

                else -> RoundedCornerShape(
                    size = ShapeRadius.ExtraSmall,
                )
            }

            val modifier =
                if (tile.enabled) {
                    Modifier
                } else {
                    Modifier
                        .graphicsLayer {
                            alpha = 0.38f
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(
                                        pass = PointerEventPass.Initial,
                                    ).changes.forEach {
                                        it.consume()
                                    }
                                }
                            }
                        }
                }

            Box(
                modifier = modifier,
            ) {
                when (tile) {
                    is SettingTile.TextTile -> TextTile(
                        description = tile.description,
                        descriptionMaxLines = tile.descriptionMaxLines,
                        headline = tile.title,
                        itemBgColor = background,
                        leading = tile.leading,
                        shapes = shape,
                    )

                    is SettingTile.CategoryTile -> CategoryTile(
                        color = tile.color,
                        description = tile.description,
                        headline = tile.title,
                        iconColor = tile.onColor,
                        itemBgColor = background,
                        leading = tile.leading,
                        onClick = tile.onClick,
                        shapes = shape,
                    )

                    is SettingTile.DialogOptionTile -> DialogOptionTile(
                        description = tile.description,
                        dialogTitle = tile.dialogTitle,
                        headline = tile.title,
                        itemBackgroundColor = background,
                        leading = tile.leading,
                        onOptionChange = tile.onOptionChange,
                        onOptionSelected = tile.onOptionSelected,
                        options = tile.options,
                        selectedOption = tile.selectedOption,
                        shapes = shape,
                    )

                    is SettingTile.ActionTile -> ActionTile(
                        colorDesc = tile.colorDesc,
                        danger = tile.danger,
                        description = tile.description,
                        headline = tile.title,
                        itemBgColor = background,
                        leading = tile.leading,
                        onClick = tile.onClick,
                        selected = tile.selected,
                        shapes = shape,
                        trailing = tile.trailing,
                    )

                    is SettingTile.SwitchTile -> SwitchTile(
                        checked = tile.checked,
                        description = tile.description,
                        enabled = tile.enabled,
                        headline = tile.title,
                        itemBgColor = background,
                        leading = tile.leading,
                        onCheckedChange = tile.onCheckedChange,
                        shapes = shape,
                    )

                    is SettingTile.SingleSwitchTile -> SingleSwitchTile(
                        checked = tile.checked,
                        description = tile.description,
                        headline = tile.title,
                        itemBgColor = background,
                        onCheckedChange = tile.onCheckedChange,
                        leading = tile.leading,
                        enabled = tile.enabled,
                    )

                    is SettingTile.DialogTextFieldTile -> DialogTextFieldTile(
                        description = tile.description,
                        headline = tile.title,
                        initialText = tile.initialText,
                        itemBgColor = background,
                        leading = tile.leading,
                        onTextSubmitted = tile.onTextSubmitted,
                        placeholder = tile.placeholder,
                        placeholderTextField = tile.placeholderTextField,
                        shapes = shape,
                    )

                    is SettingTile.DialogSliderTile -> DialogSliderTile(
                        description = tile.description,
                        dialogTitle = tile.dialogTitle,
                        headline = tile.title,
                        initialValue = tile.initialValue,
                        isDescriptionAsValue = tile.isDescriptionAsValue,
                        itemBgColor = background,
                        labelFormatter = tile.labelFormatter,
                        leading = tile.leading,
                        onValueChange = tile.onValueChange,
                        onValueSubmitted = tile.onValueSubmitted,
                        shapes = shape,
                        steps = tile.steps,
                        valueRange = tile.valueRange,
                    )
                }
            }
        }
    }
}
