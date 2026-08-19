package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface AccountOptionsProvider {
    val accountOptions: Flow<List<Option<Long>>>
}
