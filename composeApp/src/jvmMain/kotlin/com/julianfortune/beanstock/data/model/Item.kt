package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity
import com.julianfortune.beanstock.data.common.NamedEntity

data class Item(
    override val id: Long,
    override val name: String,
    val categories: List<Category>,
    val format: Format
) : NamedEntity {

    sealed interface Format {
        data object Loose : Format
        data class Packaged(val sizes: Set<Weight>) : Format {
            init {
                require(sizes.isNotEmpty()) { "`sizes` must have at least one element" }
            }
        }
    }

}
