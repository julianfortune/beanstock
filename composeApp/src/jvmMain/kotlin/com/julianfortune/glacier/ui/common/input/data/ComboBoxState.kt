package com.julianfortune.glacier.ui.common.input.data

import androidx.compose.foundation.text.input.TextFieldState
import com.julianfortune.glacier.ui.common.data.Dynamic
import com.julianfortune.glacier.ui.common.data.Option
import com.julianfortune.glacier.ui.common.input.ComboBoxDefaults


enum class ComboBoxIcon {
    CARET,
    LOADING,
    CLEAR,
    ;
}

data class ComboBoxState<ID>(
    val textFieldState: TextFieldState = TextFieldState(),
    val selection: Dynamic<Option<ID>?> = Dynamic.Present(null),
    val placeholder: String = ComboBoxDefaults.Text.PROMPT,
    val comboBoxIcon: ComboBoxIcon = ComboBoxIcon.CARET,
    val options: Dynamic<List<Option<ID>>> = Dynamic.Present(emptyList()),
    val enabled: Boolean = true,
    val expanded: Boolean = false,
    val canCreateNew: Boolean = false,
)
