package com.julianfortune.beanstock.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option
import com.julianfortune.beanstock.ui.common.input.data.ComboBoxIcon
import com.julianfortune.beanstock.ui.common.input.data.ComboBoxState
import com.julianfortune.beanstock.ui.common.input.data.ComboBoxUiEvent
import com.julianfortune.beanstock.ui.theme.AppPreview


object ComboBoxDefaults {
    object Text {
        const val PROMPT = "Type to search..."
        const val LOADING = "Loading..."
    }
}

/**
 * Combo Box
 *
 * A general-purpose composable for filtering and selecting
 * Heavily inspired by: https://mui.com/material-ui/react-autocomplete/
 */
@Composable
fun <ID> ComboBox(
    value: Dynamic<Option<ID>?>,
    onValueChange: (Option<ID>?) -> Unit,
    options: Dynamic<List<Option<ID>>>,
    onQueryChange: ((String?) -> Unit)? = null,
    onCreateNew: ((String) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val stateHolder = remember {
        ComboBoxStateHolder(
            value,
            options,
            coroutineScope,
            enabled,
            onValueChange,
            onQueryChange,
            onCreateNew,
        )
    }

    // Synchronize external state changes with internal state holder
    LaunchedEffect(value) { stateHolder.setSelection(value) }
    LaunchedEffect(options) { stateHolder.setOptions(options) }
    LaunchedEffect(enabled) { stateHolder.setEnabled(enabled) }

    ComboBoxUi(
        state = stateHolder.uiState,
        eventHandler = stateHolder::eventHandler,
        label = label,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <ID> ComboBoxUi(
    state: ComboBoxState<ID>,
    eventHandler: (ComboBoxUiEvent<ID>) -> Unit = {},
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = state.expanded,
        onExpandedChange = {
            // NOTE: This callback is invoked with inverse of current `expanded` on user click, and is the default
            // mechanism for opening/closing the dropdown menu; However, in our case, we handle toggling the `expanded`
            // state using focus, user edits, and onDismissRequest
        },
    ) {

        OutlinedTextField(
            state = state.textFieldState,
            label = { label?.invoke() },
            enabled = state.enabled,
            lineLimits = TextFieldLineLimits.SingleLine,
            colors = OutlinedTextFieldDefaults.colors(),
            trailingIcon = {
                when (state.comboBoxIcon) {
                    ComboBoxIcon.LOADING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    ComboBoxIcon.CLEAR -> IconButton(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .focusProperties { canFocus = false },
                        onClick = {
                            eventHandler(ComboBoxUiEvent.ClearSelection)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear selection"
                        )
                    }

                    ComboBoxIcon.CARET -> IconButton(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .focusProperties { canFocus = false },
                        onClick = {
                            // Clicking on the button when expanded already invokes `onDismissRequest` so we only need
                            // to fire an event when expanding the dropdown menu
                            if (!state.expanded) {
                                eventHandler(ComboBoxUiEvent.ToggleClicked)
                            }
                        }
                    ) {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.expanded)
                    }

                }
            },
            modifier = modifier
                .height(64.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                .onFocusChanged {
                    val event = when {
                        it.hasFocus || it.isFocused -> ComboBoxUiEvent.Focused
                        else -> ComboBoxUiEvent.Unfocused
                    }
                    eventHandler(event)
                },
        )

        // TODO(#81): Share with DropdownSelect
        ExposedDropdownMenu(
            expanded = state.expanded,
            onDismissRequest = {
                eventHandler(ComboBoxUiEvent.DismissRequested)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 5.dp,
        ) {
            when {
                state.options is Dynamic.Loading -> {
                    NoOptionsMenuItemV2("Loading...")
                }

                (state.options as Dynamic.Present).value.isEmpty() -> {
                    when {
                        state.canCreateNew -> {
                            val currentQuery = state.textFieldState.text

                            when {
                                currentQuery == "" -> NoOptionsMenuItemV2(ComboBoxDefaults.Text.PROMPT)
                                else -> CreateNewMenuItem("Create '${currentQuery}'") {
                                    eventHandler(ComboBoxUiEvent.CreateNew(state.textFieldState.text.toString()))
                                }
                            }
                        }

                        else -> NoOptionsMenuItemV2("No matches found")
                    }
                }

                else -> state.options.value.forEach { option ->
                    val isSelected = option.id == (state.selection as? Dynamic.Present)?.value?.id

                    val backgroundColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else MaterialTheme.colorScheme.surfaceContainerHigh
                    val textColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else MaterialTheme.colorScheme.onSurface

                    DropdownMenuItem(
                        text = {
                            Text(option.label)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier
                            .background(backgroundColor)
                            .pointerHoverIcon(PointerIcon.Hand),
                        colors = MenuDefaults.itemColors().copy(textColor = textColor),
                        onClick = {
                            eventHandler(
                                ComboBoxUiEvent.UpdateSelection(option)
                            )
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewMenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text) },
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        colors = MenuDefaults.itemColors(),
        onClick = onClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoOptionsMenuItemV2(text: String) {
    DropdownMenuItem(
        text = { Text(text) },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        enabled = false,
        colors = MenuDefaults.itemColors(),
        onClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DefaultPreview() = AppPreview {
    ComboBoxUi(
        ComboBoxState<Long>(),
        label = { Text("Status") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PrefilledPreview() = AppPreview {
    ComboBoxUi(
        ComboBoxState(
            textFieldState = TextFieldState("Apple"),
            selection = Dynamic.Present(Option(0L, "Apple")),
        ),
        label = { Text("Fruit") }
    )
}
