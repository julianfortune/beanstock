package com.julianfortune.beanstock.ui.common.input.data

import com.julianfortune.beanstock.ui.common.data.Option

sealed interface ComboBoxUiEvent<out ID> {
    data class UpdateSelection<ID>(val newSelection: Option<ID>) : ComboBoxUiEvent<ID>
    data object ClearSelection : ComboBoxUiEvent<Nothing>
    data object Focused : ComboBoxUiEvent<Nothing>
    data object Unfocused : ComboBoxUiEvent<Nothing>
    data object ToggleClicked : ComboBoxUiEvent<Nothing>
    data object DismissRequested : ComboBoxUiEvent<Nothing>
    data class CreateNew(val name: String) : ComboBoxUiEvent<Nothing>
}
