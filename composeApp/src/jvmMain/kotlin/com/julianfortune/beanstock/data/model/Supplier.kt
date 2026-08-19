package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.NamedEntity

data class Supplier(
    override val id: Long,
    override val name: String,
) : NamedEntity
