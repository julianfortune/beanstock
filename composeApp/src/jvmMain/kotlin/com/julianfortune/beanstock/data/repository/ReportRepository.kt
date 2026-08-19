package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.julianfortune.beanstock.core.util.unwrapUnsafe
import com.julianfortune.beanstock.data.codec.CostStatusCodec
import com.julianfortune.beanstock.data.codec.LocalDateCodec
import com.julianfortune.beanstock.data.common.EntityMetadata
import com.julianfortune.beanstock.data.model.*
import com.julianfortune.beanstock.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate


class ReportRepository(private val database: Database) {

    fun getAllAsHeadlines(): Flow<List<ReportHeadline>> {
        return database.basicReportQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { reports ->
                reports.map {
                    // TODO(P3): Error handling
                    val start = LocalDateCodec.deserialize(it.startDate).unwrapUnsafe()
                    val end = LocalDateCodec.deserialize(it.endDate).unwrapUnsafe()

                    ReportHeadline(it.id, it.name, start, end)
                }
            }
    }

    fun getById(id: Long): Flow<Report?> = database.basicReportQueries.getByIdWithHydration(id)
        .asFlow()
        .mapToOneOrNull(Dispatchers.IO)
        .map { row ->
            when (row) {
                null -> null
                else -> {
                    val start = LocalDateCodec.deserialize(row.startDate).unwrapUnsafe()
                    val end = LocalDateCodec.deserialize(row.endDate).unwrapUnsafe()

                    val item = row.itemId?.let { itemId ->
                        ItemHeadline(
                            itemId,
                            row.itemName ?: throw Exception("`itemName` must be defined by foreign key constraints")
                        )
                    }
                    val category = row.itemCategoryId?.let { categoryId ->
                        Category(
                            categoryId,
                            row.categoryName
                                ?: throw Exception("`categoryName` must be defined by foreign key constraints")
                        )
                    }
                    val costStatus = row.costStatus?.let { CostStatusCodec.deserialize(it).unwrapUnsafe() }
                    val program = row.programId?.let { id ->
                        Program(
                            id,
                            row.programName
                                ?: throw Exception("`programName` must be defined by foreign key constraints")
                        )
                    }
                    val account = row.purchasingAccountId?.let { id ->
                        Account(
                            id,
                            row.purchasingAccountName
                                ?: throw Exception("`purchasingAccountName` must be defined by foreign key constraints")
                        )
                    }
                    val supplier = row.supplierId?.let { id ->
                        Supplier(
                            id,
                            row.supplierName
                                ?: throw Exception("`supplierName` must be defined by foreign key constraints")
                        )
                    }

                    val metadata = EntityMetadata.ofEpochSeconds(row.createdAtEpochSeconds, row.updatedAtEpochSeconds)

                    Report(
                        row.id,
                        row.name,
                        Report.Criteria(
                            start,
                            end,
                            item,
                            category,
                            costStatus,
                            program,
                            account,
                            supplier,
                        ),
                        metadata
                    )
                }
            }
        }

    suspend fun insert(
        name: String,
        start: LocalDate,
        end: LocalDate,
        itemId: Long?,
        itemCategoryId: Long?,
        costStatus: CostStatus?,
        programId: Long?,
        purchasingAccountId: Long?,
        supplierId: Long?,
    ): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            database.basicReportQueries.insert(
                name,
                LocalDateCodec.serialize(start),
                LocalDateCodec.serialize(end),
                itemId,
                itemCategoryId,
                costStatus?.let { CostStatusCodec.serialize(it) },
                programId,
                purchasingAccountId,
                supplierId,
                now.epochSecond,
                now.epochSecond,
            ).awaitAsOne()
        }
    }

    suspend fun update(
        id: Long,
        name: String,
        start: LocalDate,
        end: LocalDate,
        itemId: Long?,
        itemCategoryId: Long?,
        costStatus: CostStatus?,
        programId: Long?,
        purchasingAccountId: Long?,
        supplierId: Long?,
    ): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            val rowsUpdated = database.basicReportQueries.updateById(
                name,
                LocalDateCodec.serialize(start),
                LocalDateCodec.serialize(end),
                itemId,
                itemCategoryId,
                costStatus?.let { CostStatusCodec.serialize(it) },
                programId,
                purchasingAccountId,
                supplierId,
                now.epochSecond,
                id,
            )

            return when {
                rowsUpdated > 0 -> Result.success(id)
                else -> Result.failure(IllegalStateException("BasicReport with id=$id could not be updated"))
            }
        }
    }

    suspend fun updateName(
        id: Long,
        name: String,
    ): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            val rowsUpdated = database.basicReportQueries.updateNameById(
                name,
                now.epochSecond,
                id,
            )

            return when {
                rowsUpdated > 0 -> Result.success(id)
                else -> Result.failure(IllegalStateException("BasicReport with id=$id could not be updated"))
            }
        }
    }

    suspend fun delete(id: Long): Result<Long> {
        val rowsDeleted = database.basicReportQueries.deleteById(id)

        return when {
            rowsDeleted > 0 -> Result.success(id)
            else -> Result.failure(IllegalStateException("BasicReport with id=$id could not be deleted"))
        }
    }
}
