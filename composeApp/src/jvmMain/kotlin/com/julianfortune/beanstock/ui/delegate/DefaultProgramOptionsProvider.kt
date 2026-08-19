package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.data.repository.ProgramRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DefaultProgramOptionsProvider(
    programRepository: ProgramRepository,
    scope: CoroutineScope
) : ProgramOptionsProvider {

    override val programOptions = programRepository.getAll()
        .map { entities -> entities.map { it.toOption() } }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

}
