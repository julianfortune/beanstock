package com.julianfortune.beanstock.data.model

import com.julianfortune.beanstock.data.common.Entity
import com.julianfortune.beanstock.data.common.EntityMetadata
import java.time.LocalDate

data class DeliveryHeadline(
    override val id: Long,
    val received: LocalDate,
    val supplier: Supplier,
    val taxesCents: Long?,
    val feesCents: Long?,
    val metadata: EntityMetadata,
) : Entity
