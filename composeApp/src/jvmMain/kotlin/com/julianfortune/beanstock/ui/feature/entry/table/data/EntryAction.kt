package com.julianfortune.beanstock.ui.feature.entry.table.data

import com.julianfortune.beanstock.ui.feature.entry.form.data.EntryBody

sealed interface EntryAction {
    data object Add : EntryAction
    data class Edit(val id: Long, val entry: EntryBody) : EntryAction
    data class Delete(val id: Long) : EntryAction
}
