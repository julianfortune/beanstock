package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.data.model.Category
import com.julianfortune.beanstock.data.repository.NamedEntityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultCategoryOptionsProvider(
    categoryRepository: NamedEntityRepository<Category>,
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
