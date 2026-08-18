package com.julianfortune.glacier.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.theme.AppPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <ID> DropdownSelect(
    selectedId: ID,
    options: List<Option<ID>>,
    onSelectedChange: (Option<ID>) -> Unit,
    label: String? = null,
    // TODO: Make the enabled=false state look visually disabled
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedOption = remember(selectedId, options) {
        options.firstOrNull { it.id == selectedId }
            ?: throw IndexOutOfBoundsException("SelectedId ($selectedId) does not correspond to any value in `options`")
    }

    LaunchedEffect(enabled) {
        if (!enabled) {
            expanded = false
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        },
        modifier = modifier
    ) {
        // TODO: Use a textbox (or find a way to reuse `ComboBox` for the UI ?)
        Surface(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = enabled)
                .pointerHoverIcon(if (enabled) PointerIcon.Hand else PointerIcon.Default),
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    label?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Text(
                        text = selectedOption.label,
                    )
                }
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        }

        // TODO(#79): Share with ComboBox
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val isSelected = option.id == selectedOption.id

                // Use colors to highlight selected item
                val backgroundColor = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else MenuDefaults.containerColor
                val textColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else MaterialTheme.colorScheme.onSurface

                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .background(backgroundColor)
                        .pointerHoverIcon(PointerIcon.Hand),
                    colors = MenuDefaults.itemColors().copy(textColor = textColor),
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview
@Composable
fun DropdownSelectPreview() = AppPreview {
    Column {
        DropdownSelect(
            0,
            listOf(Option(0, "Example")),
            {},
            label = "Choice"
        )
    }
}