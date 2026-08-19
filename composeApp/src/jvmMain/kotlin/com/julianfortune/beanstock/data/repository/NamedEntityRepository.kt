package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.beanstock.data.common.NamedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface NamedEntityRepository<A : Any, T : NamedEntity> {

    val entityName: String

    val getAllFn: () -> Query<A>
    val insertFn: (name: String) -> ExecutableQuery<Long>
    val updateFn: suspend (name: String, id: Long) -> Long
    val deleteFn: suspend (id: Long) -> Long

    fun fromRow(row: A): T

    fun getAll(): Flow<List<T>> {
        return getAllFn()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.map {
                    fromRow(it)
                }
            }
    }


    suspend fun insert(name: String): Result<Long> {
        return Result.runCatching {
            insertFn(name).awaitAsOne()
        }
    }

    suspend fun update(id: Long, name: String): Result<Long> {
        return Result.runCatching {
            updateFn(name, id)
        }.fold(
            onSuccess = { rowsUpdated ->
                if (rowsUpdated > 0) Result.success(id)
                else Result.failure(IllegalStateException("$entityName with id=$id could not be updated"))
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun delete(id: Long): Result<Long> {
        return Result.runCatching {
            deleteFn(id)
        }.fold(
            onSuccess = { rowsDeleted ->
                if (rowsDeleted > 0) Result.success(id)
                else Result.failure(IllegalStateException("$entityName with id=$id could not be deleted"))
            },
            onFailure = { Result.failure(it) }
        )
    }
}
