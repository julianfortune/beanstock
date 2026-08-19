package com.julianfortune.beanstock.ui.delegate

import com.julianfortune.beanstock.ui.common.data.Option
import kotlinx.coroutines.flow.Flow

interface SupplierOptionsProvider {
    val supplierOptions: Flow<List<Option<Long>>>
}
