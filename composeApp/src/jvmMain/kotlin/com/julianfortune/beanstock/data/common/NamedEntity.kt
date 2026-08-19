package com.julianfortune.beanstock.data.common

import com.julianfortune.beanstock.ui.common.data.Option

interface NamedEntity : Entity {
    val name: String

    fun toOption(): Option<Long> = Option(id, name)
}
