package com.julianfortune.beanstock.data.common

import java.time.Instant

data class EntityMetadata(
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun ofEpochSeconds(createdAtEpochSeconds: Long, updatedAtEpochSeconds: Long) = EntityMetadata(
            Instant.ofEpochSecond(createdAtEpochSeconds),
            Instant.ofEpochSecond(createdAtEpochSeconds)
        )
    }
}
