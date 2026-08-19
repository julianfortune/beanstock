package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity
import com.julianfortune.beanstock.data.common.EntityMetadata
import java.time.LocalDate

data class Report(
    override val id: Long,
    val name: String,
    val criteria: Criteria,
    val metadata: EntityMetadata,
) : Entity {
    data class Criteria(
        val start: LocalDate,
        val end: LocalDate,
        val item: ItemHeadline?,
        val category: Category?,
        val costStatus: CostStatus?,
        val program: Program?,
        val account: Account?,
        val supplier: Supplier?,
    )
}
