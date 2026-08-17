package com.julianfortune.glacier.ui.common.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.julianfortune.glacier.ui.common.data.Dynamic
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.data.ComboBoxIcon
import com.julianfortune.glacier.ui.common.input.data.ComboBoxState
import com.julianfortune.glacier.ui.common.input.data.ComboBoxUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class ComboBoxStateHolder<ID>(
    initialSelection: Dynamic<Option<ID>?>,
    initialOptions: Dynamic<List<Option<ID>>>,
    scope: CoroutineScope,
    initialEnabled: Boolean = true,
    private val onSelectionChange: (Option<ID>?) -> Unit,
    private val onQueryChange: ((String?) -> Unit)? = null,
    private val onCreateNew: ((String?) -> Unit)? = null,
) {
    private val textFieldState = TextFieldState(getTitle(initialSelection))

    // Inelegant mechanism for distinguishing user vs. programmatic mutations of `textFieldState`
    private var isProgrammaticEditing: Boolean by mutableStateOf(
        true // The initial state is set programmatically
    )

    // NOTE: `_` prevents kotlin-generated setters from clashing with our methods below
    private var _selection: Dynamic<Option<ID>?> by mutableStateOf(initialSelection)

    private var _options: Dynamic<List<Option<ID>>> by mutableStateOf(initialOptions)
    private var _enabled by mutableStateOf(initialEnabled)
    private var _expanded by mutableStateOf(false)

    init {
        snapshotFlow { textFieldState.text }
            .onEach { newText ->
                onQueryChange?.invoke(newText.toString())

                if (!isProgrammaticEditing) {
                    _expanded = true
                    onQueryChange?.invoke(newText.toString())
                }

                isProgrammaticEditing = false
            }
            .launchIn(scope)
    }

    fun eventHandler(uiEvent: ComboBoxUiEvent<ID>) = when (uiEvent) {
        is ComboBoxUiEvent.ToggleClicked -> _expanded = true // Only opens
        is ComboBoxUiEvent.DismissRequested -> _expanded = false
        is ComboBoxUiEvent.Focused -> _expanded = true
        is ComboBoxUiEvent.Unfocused -> _expanded = false

        is ComboBoxUiEvent.UpdateSelection<ID> -> {
            _expanded = false
            onSelectionChange(uiEvent.newSelection)
        }

        ComboBoxUiEvent.ClearSelection -> {
            onSelectionChange(null)
        }

        is ComboBoxUiEvent.CreateNew -> {
            onCreateNew?.invoke(uiEvent.name)
        }
    }

    fun setSelection(newSelection: Dynamic<Option<ID>?>) {
        _selection = newSelection

        val newTitle = getTitle(newSelection)
        if (newTitle != textFieldState.text) {
            isProgrammaticEditing = true
            textFieldState.setTextAndPlaceCursorAtEnd(newTitle)
        }
    }

    fun setOptions(newOptions: Dynamic<List<Option<ID>>>) {
        _options = newOptions
    }

    fun setEnabled(enabled: Boolean) {
        _enabled = enabled
    }

    private fun getTitle(selection: Dynamic<Option<ID>?>): String {
        return when (selection) {
            Dynamic.Loading -> ComboBoxDefaults.Text.LOADING
            is Dynamic.Present -> {
                selection.value?.title ?: ""
            }
        }
    }

    val uiState: ComboBoxState<ID> by derivedStateOf {
        val placeholder = ComboBoxDefaults.Text.PROMPT
        val trailingIcon = when {
            _selection is Dynamic.Loading -> ComboBoxIcon.LOADING
            (_selection as? Dynamic.Present)?.value != null -> ComboBoxIcon.CLEAR
            else -> ComboBoxIcon.CARET
        }
        // Disable interactions if selection is loading
        val enabled = _selection is Dynamic.Present && _enabled

        ComboBoxState(
            textFieldState,
            _selection,
            placeholder,
            trailingIcon,
            _options,
            enabled,
            _expanded,
            canCreateNew = onCreateNew != null
        )
    }
}
