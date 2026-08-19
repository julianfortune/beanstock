package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.data.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultAccountOptionsProvider(
    accountRepository: AccountRepository,
    scope: CoroutineScope
) : AccountOptionsProvider {

    override val accountOptions = accountRepository.getAll()
        .map { entities -> entities.map { it.toOption() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
