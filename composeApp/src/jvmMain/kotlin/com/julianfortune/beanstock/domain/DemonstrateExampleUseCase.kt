package com.julianfortune.beanstock.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class DemonstrateExampleUseCase(
    // Inject repositories (and/or other use-cases)
) {

    /**
     * Perform some complex or shared logic
     */
    operator fun invoke(): Flow<List<String>> =
        // TODO ...
        emptyFlow()

}