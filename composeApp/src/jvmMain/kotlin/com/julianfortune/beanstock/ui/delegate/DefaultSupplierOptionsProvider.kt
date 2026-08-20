package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.data.model.Supplier
import com.julianfortune.beanstock.data.repository.NamedEntityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultSupplierOptionsProvider(
    supplierRepository: NamedEntityRepository<Supplier>,
    scope: CoroutineScope
) : SupplierOptionsProvider {

    override val supplierOptions = supplierRepository.getAll()
        .map { entities -> entities.map { it.toOption() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
