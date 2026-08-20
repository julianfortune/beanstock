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

class GenericNamedEntityRepository<ROW : Any, ENTITY : NamedEntity>(
    val entityName: String,
    private val getAllFn: () -> Query<ROW>,
    private val insertFn: (name: String) -> ExecutableQuery<Long>,
    private val updateFn: suspend (name: String, id: Long) -> Long,
    private val deleteFn: suspend (id: Long) -> Long,
    private val fromRow: (row: ROW) -> ENTITY,
) : NamedEntityRepository<ENTITY> {

    override fun getAll(): Flow<List<ENTITY>> {
        return getAllFn()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.map {
                    fromRow(it)
                }
            }
    }

    override fun getById(id: Long): Flow<ENTITY> {
        TODO("Not yet implemented")
    }

    override fun searchByName(query: String): Flow<List<ENTITY>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(name: String): Result<Long> {
        return Result.runCatching {
            insertFn(name).awaitAsOne()
        }
    }

    override suspend fun updateNameById(id: Long, name: String): Result<Long> {
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

    override suspend fun deleteById(id: Long): Result<Long> {
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
