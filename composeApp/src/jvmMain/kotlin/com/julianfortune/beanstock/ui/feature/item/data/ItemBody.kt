package com.julianfortune.beanstock.ui.feature.item.data

import com.julianfortune.beanstock.data.model.Item

data class ItemBody(
    val name: String,
    val categoryId: Long?,
    val format: Item.Format,
)
