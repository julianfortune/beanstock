package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity

data class Item(
    override val id: Long,
    val name: String,
    val categories: List<Category>,
    val format: Format
) : Entity {

    sealed interface Format {
        data object Loose : Format
        data class Packaged(val sizes: Set<Weight>) : Format {
            init {
                require(sizes.isNotEmpty()) { "`sizes` must have at least one element" }
            }
        }
    }

}
