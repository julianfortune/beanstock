package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity

data class ItemHeadline(
    override val id: Long,
    val name: String,
) : Entity
