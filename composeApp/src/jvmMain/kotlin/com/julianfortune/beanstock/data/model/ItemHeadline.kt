package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity
import com.julianfortune.beanstock.data.common.NamedEntity

data class ItemHeadline(
    override val id: Long,
    override val name: String,
) : NamedEntity
