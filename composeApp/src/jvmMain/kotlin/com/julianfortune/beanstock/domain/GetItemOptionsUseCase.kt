package com.julianfortune.beanstock.domain

import com.julianfortune.beanstock.data.repository.ItemRepository
import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetItemOptionsUseCase(private val itemRepository: ItemRepository) {

    operator fun invoke(query: String?): Flow<List<Option<Long>>> {
        val itemHeadlines = when (query) {
            null -> itemRepository.getAll()
            else -> itemRepository.searchByName(query)
        }

        return itemHeadlines.map { headlines ->
            headlines.map { Option(it.id, it.name) }
        }
    }

}
