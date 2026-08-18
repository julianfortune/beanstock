package com.julianfortune.glacier.ui.common.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.julianfortune.glacier.ui.common.data.Dynamic
import com.julianfortune.glacier.ui.common.data.Option
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun <ID> AsyncCreatableSelect(
    value: Dynamic<Option<ID>?>, // Selected ID
    onValueChange: (ID?) -> Unit,
    getOptions: (query: String?) -> Flow<List<Option<ID>>>,
    onCreateNew: (name: String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    debounceTimeout: Duration,
    modifier: Modifier = Modifier,
) {
    val stateHolder = remember(getOptions, debounceTimeout) {
        AsyncDynamicOptionsStateHolder(getOptions, debounceTimeout)
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
