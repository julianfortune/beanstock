package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface CategoryOptionsProvider {
    val categoryOptions: Flow<List<Option<Long>>>
}
