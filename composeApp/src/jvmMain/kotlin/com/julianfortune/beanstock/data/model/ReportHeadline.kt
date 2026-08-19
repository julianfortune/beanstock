package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity
import java.time.LocalDate

data class ReportHeadline(
    override val id: Long,
    val name: String,
    val start: LocalDate,
    val end: LocalDate,
) : Entity
