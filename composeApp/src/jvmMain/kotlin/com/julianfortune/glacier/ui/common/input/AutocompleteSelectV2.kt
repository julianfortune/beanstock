package com.julianfortune.glacier.ui.common.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.theme.AppPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


// TODO:
//  - Make dynamic (loading states for value and options)
//  - Split into multiple files
//  - Write a custom wrapper (ComboBox) that maps to the old interface `AutocompleteSelect`
//  - Write a custom wrapper (AsyncAutocomplete) that will work easily for my async (takes just `id: Long` and a use-case)

/**
 * Heavily inspired by: https://mui.com/material-ui/react-autocomplete/
 */

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    FlowPreview::class,
    ExperimentalCoroutinesApi::class,
)
@Composable
fun <ID> AutocompleteSelectV2(
    value: Option<ID>?, // TODO: Dynamic
    onValueChange: (Option<ID>?) -> Unit,
    options: List<Option<ID>>, // TODO: Dynamic
    onQueryChange: ((String?) -> Unit)? = null,
    onCreateNew: ((String?) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val stateHolder = remember {
        AutocompleteStateHolder(
            value,
            options,
            coroutineScope,
            onValueChange,
            onQueryChange,
            onCreateNew,
        )
    }

    // Synchronize external state changes with internal state holder
    LaunchedEffect(value) { stateHolder.setSelection(value) }
    LaunchedEffect(options) { stateHolder.setOptions(options) }

    AutocompleteSelectV2Ui(
        state = stateHolder.uiState,
        eventHandler = stateHolder::eventHandler,
        label = label,
        enabled = enabled,
        modifier = modifier
    )
}

object AutocompleteSelectV2Defaults {
    object Text {
        const val PROMPT = "Type to search..."
        const val LOADING = "Loading..."
    }
}

enum class TrailingIcon {
    CARET,
    LOADING,
    CLEAR,
    ;
}

data class AutocompleteSelectV2State<ID>(
    val textFieldState: TextFieldState = TextFieldState(),
    val selection: Option<ID>? = null, // TODO: Dynamic
    val placeholder: String = AutocompleteSelectV2Defaults.Text.PROMPT,
    val trailingIcon: TrailingIcon = TrailingIcon.CARET,
    val options: List<Option<ID>> = emptyList(),  // TODO: Dynamic
    val expanded: Boolean = false,
    val canCreateNew: Boolean = false, // TODO
)

sealed interface AutocompleteSelectUiEvent<out ID> {
    data class UpdateSelection<ID>(val newSelection: Option<ID>) : AutocompleteSelectUiEvent<ID>
    data object ClearSelection : AutocompleteSelectUiEvent<Nothing>
    data object Focused : AutocompleteSelectUiEvent<Nothing>
    data object Unfocused : AutocompleteSelectUiEvent<Nothing>
    data object ToggleClicked : AutocompleteSelectUiEvent<Nothing>
    data object DismissRequested : AutocompleteSelectUiEvent<Nothing>
    data class CreateNew(val name: String) : AutocompleteSelectUiEvent<Nothing>
}

class AutocompleteStateHolder<ID>(
    initialSelection: Option<ID>?,
    initialOptions: List<Option<ID>>,
    scope: CoroutineScope,
    private val onSelectionChange: (Option<ID>?) -> Unit,
    private val onQueryChange: ((String?) -> Unit)? = null,
    private val onCreateNew: ((String?) -> Unit)? = null,
) {
    val initialText = initialSelection?.title ?: ""

    // === State management ===
    private val textFieldState = TextFieldState(initialText)

    // Inelegant mechanism for distinguishing user vs. programmatic mutations of `textFieldState`
    private var _programmaticEditing: Boolean by mutableStateOf(
        true // The initial state is set programmatically
    )

    private var _selection: Option<ID>? by mutableStateOf(initialSelection)
    private var _options: List<Option<ID>> by mutableStateOf(initialOptions)

    var expanded by mutableStateOf(false)
        private set

    init {
        snapshotFlow { textFieldState.text }
            .onEach { newText ->
                onQueryChange?.invoke(newText.toString())

                if (!_programmaticEditing) {
                    expanded = true
                    onQueryChange?.invoke(newText.toString())
                }

                _programmaticEditing = false
            }
            .launchIn(scope)
    }

    fun eventHandler(uiEvent: AutocompleteSelectUiEvent<ID>) = when (uiEvent) {
        is AutocompleteSelectUiEvent.ToggleClicked -> expanded = true // Only opens
        is AutocompleteSelectUiEvent.DismissRequested -> expanded = false
        is AutocompleteSelectUiEvent.Focused -> expanded = true
        is AutocompleteSelectUiEvent.Unfocused -> expanded = false
        is AutocompleteSelectUiEvent.UpdateSelection<ID> -> onSelectionChange(uiEvent.newSelection)
        AutocompleteSelectUiEvent.ClearSelection -> TODO()
        is AutocompleteSelectUiEvent.CreateNew -> TODO()
    }

    fun setSelection(newSelection: Option<ID>?) {
        _selection = newSelection
        expanded = false

        val newTitle = newSelection?.title ?: ""
        if (newTitle != textFieldState.text) {
            _programmaticEditing = true
            textFieldState.setTextAndPlaceCursorAtEnd(newTitle)
        }
    }

    fun setOptions(newOptions: List<Option<ID>>) {
        _options = newOptions
    }

    val uiState: AutocompleteSelectV2State<ID> by derivedStateOf {
        val placeholder = AutocompleteSelectV2Defaults.Text.PROMPT // TODO
        val trailingIcon = TrailingIcon.CARET // TODO

        AutocompleteSelectV2State(
            textFieldState,
            _selection,
            placeholder,
            trailingIcon,
            _options,
            expanded,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <ID> AutocompleteSelectV2Ui(
    state: AutocompleteSelectV2State<ID>,
    eventHandler: (AutocompleteSelectUiEvent<ID>) -> Unit = {},
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = enabled && state.expanded,
        onExpandedChange = {
            // NOTE: This callback is invoked with inverse of current `expanded` on user click, and is the default
            // mechanism for opening/closing the dropdown menu; However, in our case, we handle toggling the `expanded`
            // state using focus, user edits, and onDismissRequest
        },
    ) {

        OutlinedTextField(
            state = state.textFieldState,
            label = { label?.invoke() },
            enabled = enabled,
            lineLimits = TextFieldLineLimits.SingleLine,
            placeholder = { Text(state.placeholder) },
            colors = OutlinedTextFieldDefaults.colors(),
            trailingIcon = {
                when (state.trailingIcon) {
                    TrailingIcon.LOADING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TrailingIcon.CLEAR -> IconButton(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .focusProperties { canFocus = false },
                        onClick = {
                            eventHandler.invoke(AutocompleteSelectUiEvent.ClearSelection)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear selection"
                        )
                    }

                    TrailingIcon.CARET -> IconButton(
                        modifier = Modifier
                            .size(28.dp)
                            .pointerHoverIcon(PointerIcon.Hand)
                            .focusProperties { canFocus = false },
                        onClick = {
                            // Clicking on the button when expanded already invokes `onDismissRequest` so we only need
                            // to fire an event when expanding the dropdown menu
                            if (!state.expanded) {
                                eventHandler.invoke(AutocompleteSelectUiEvent.ToggleClicked)
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
                        it.hasFocus || it.isFocused -> AutocompleteSelectUiEvent.Focused
                        else -> AutocompleteSelectUiEvent.Unfocused
                    }
                    eventHandler.invoke(event)
                },
        )

        ExposedDropdownMenu(
            expanded = enabled && state.expanded,
            onDismissRequest = {
                eventHandler.invoke(AutocompleteSelectUiEvent.DismissRequested)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 5.dp,
        ) {
            when {
                state.options.isEmpty() -> NoOptionsMenuItemV2("No matches found")
                else -> state.options.forEach { option ->
                    val isSelected = option.id == state.selection?.id

                    val backgroundColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else MaterialTheme.colorScheme.surfaceContainerHigh
                    val textColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else MaterialTheme.colorScheme.onSurface

                    DropdownMenuItem(
                        text = {
                            Text(option.title)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        modifier = Modifier
                            .background(backgroundColor)
                            .pointerHoverIcon(PointerIcon.Hand),
                        colors = MenuDefaults.itemColors().copy(textColor = textColor),
                        onClick = {
                            eventHandler.invoke(
                                AutocompleteSelectUiEvent.UpdateSelection(option)
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
    AutocompleteSelectV2Ui(
        AutocompleteSelectV2State<Long>(
            placeholder = "Type to search"
        ),
        label = { Text("Status") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PrefilledPreview() = AppPreview {
    AutocompleteSelectV2Ui(
        AutocompleteSelectV2State(
            textFieldState = TextFieldState("Apple"),
            selection = Option(0L, "Apple"),
            placeholder = "Type to search",
        ),
        label = { Text("Fruit") }
    )
}
