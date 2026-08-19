package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.data.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultCategoryOptionsProvider(
    categoryRepository: CategoryRepository,
    scope: CoroutineScope
) : CategoryOptionsProvider {

    override val categoryOptions = categoryRepository.getAll()
        .map { entities -> entities.map { it.toOption() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
