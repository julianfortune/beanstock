package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.data.repository.ItemRepository
import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultItemOptionsProvider(
    itemRepository: ItemRepository,
    scope: CoroutineScope
) : ItemOptionsProvider {

    override val itemOptions = itemRepository.getAll()
        .map { items ->
            items.map { Option(it.id, it.name) }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
