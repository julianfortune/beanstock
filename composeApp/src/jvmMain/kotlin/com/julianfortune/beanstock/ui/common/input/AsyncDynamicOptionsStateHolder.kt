package com.julianfortune.beanstock.ui.common.input

import com.julianfortune.beanstock.core.util.throttleLatest
import com.julianfortune.beanstock.ui.common.data.Dynamic
import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AsyncDynamicOptionsStateHolder<ID>(
    getOptions: (query: String?) -> Flow<List<Option<ID>>>,
    debounceTimeout: Duration = 300.milliseconds,
) {
    private val _queryFlow = MutableSharedFlow<String?>(extraBufferCapacity = 2)
    val queryFlow = _queryFlow
        .asSharedFlow()
        // Prime state with an initial query to load options without the user having to type anything
        .onStart { emit(null) }
        .distinctUntilChanged()

    private val optionsForQueryFlow = queryFlow
        // TODO: It would be awesome to 'delay the emission' of Loading instead of throttling typing updates
        //  so the user would only see the 'Loading' state if the query took longer than 100ms (for some reason)
        .throttleLatest(debounceTimeout)
        .flatMapLatest { query ->
            getOptions(query).map {
                Pair(query, it)
            }
        }

    val options = combine(queryFlow, optionsForQueryFlow) { currentQuery, (loadedQuery, loadedOptions) ->
        when (loadedQuery) {
            currentQuery -> Dynamic.Present(loadedOptions)
            else -> Dynamic.Loading
        }
    }

    fun onQueryChanged(newQuery: String?) {
        _queryFlow.tryEmit(newQuery)
    }
}
