package com.julianfortune.beanstock.ui.common.input

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option


/**
 * Wrapper around a generic combo-box for selecting from a static list of options (with user typing to filter)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun <ID> AutocompleteSelect(
    selectedOptionId: ID?,
    options: List<Option<ID>>,
    onSelectedChange: (Option<ID>?) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var query by remember {
        mutableStateOf<String?>(null)
    }

    val selectedOption: Option<ID>? = remember(selectedOptionId, options) {
        selectedOptionId?.let {
            options.firstOrNull { it.id == selectedOptionId }
        }
    }

    val filteredOptions = remember(query, options) {
        if (query != null) {
            options.filter { option ->
                option.label.contains(query!!, ignoreCase = true)
            }
        } else {
            options
        }
    }

    ComboBox(
        Dynamic.Present(selectedOption),
        onValueChange = {
            onSelectedChange(it)
        },
        options = Dynamic.Present(filteredOptions),
        onQueryChange = {
            query = it
        },
        label = label,
        modifier = modifier,
    )
}
