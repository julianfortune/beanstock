package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface ProgramOptionsProvider {
    val programOptions: Flow<List<Option<Long>>>
}
