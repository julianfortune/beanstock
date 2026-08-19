package com.julianfortune.beanstock.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.theme.AppPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <ID> DropdownSelect(
    selectedId: ID,
    options: List<Option<ID>>,
    onSelectedChange: (Option<ID>) -> Unit,
    label: String? = null,
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
        modifier = modifier.pointerHoverIcon(
            when {
                enabled -> PointerIcon.Hand
                else -> PointerIcon.Default
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        ) {
            OutlinedTextField(
                value = selectedOption.label,
                onValueChange = {},
                readOnly = true,
                label = label?.let { { Text(label) } },
                enabled = enabled,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.height(64.dp).fillMaxWidth()
            )

            // Transparent overlay to prevent the user able to interact with text box at all
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(interactionSource = null, indication = null, onClick = {}),
            )
        }

        // TODO(#81): Share with ComboBox
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
