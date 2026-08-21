package com.julianfortune.beanstock.ui.common.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun <ID> AsyncAutocompleteSelect(
    value: Dynamic<Option<ID>?>, // Selected ID
    onValueChange: (ID?) -> Unit, // TODO(?): Option<ID>
    getOptions: (query: String?) -> Flow<List<Option<ID>>>,
    onCreateNew: ((name: String) -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    throttleTimeout: Duration = 300.milliseconds,
    modifier: Modifier = Modifier,
) {
    val stateHolder = remember(getOptions, throttleTimeout) {
        AsyncDynamicOptionsStateHolder(getOptions, throttleTimeout)
    }

    val options by stateHolder.options.collectAsState(Dynamic.Loading)

    ComboBox(
        value,
        onValueChange = { onValueChange(it?.id) },
        options = options,
        onQueryChange = stateHolder::onQueryChanged,
        // TODO: Might want to have 'Create New' always show up just in case `Production` exists but we want to add `Product`
        onCreateNew = onCreateNew,
        label = label,
        modifier = modifier,
    )
}
