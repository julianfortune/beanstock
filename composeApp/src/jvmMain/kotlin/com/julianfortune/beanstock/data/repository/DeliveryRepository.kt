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
import com.julianfortune.beanstock.db.GetAllByBasicReportCriteriaPaginated
import com.julianfortune.beanstock.db.GetByDeliveryId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate

class DeliveryRepository(private val database: Database) {

    fun getAllAsHeadlines(): Flow<List<DeliveryHeadline>> {
        return database.deliveryQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { deliveries ->
                deliveries.map { d ->
                    // TODO(P3): Better error handling
                    val receivedDate = LocalDateCodec.deserialize(d.receivedDate).unwrapUnsafe()
                    val supplier = supplierFromJoinedRow(d.supplierId, d.supplierName)
                    val metadata = EntityMetadata.ofEpochSeconds(d.createdAtEpochSeconds, d.updatedAtEpochSeconds)

                    DeliveryHeadline(d.id, receivedDate, supplier, d.taxesCents, d.feesCents, metadata)
                }
            }
    }

    fun getDeliveryById(deliveryId: Long): Flow<Delivery?> {
        val deliveryFlow = database.deliveryQueries.getById(deliveryId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)

        val entriesFlow = database.deliveryEntryQueries.getByDeliveryId(deliveryId)
            .asFlow()
            .mapToList(Dispatchers.IO)

        return combine(deliveryFlow, entriesFlow) { deliveryRow, entryRows ->
            if (deliveryRow == null) {
                return@combine null
            }

            val supplier = supplierFromJoinedRow(deliveryRow.supplierId, deliveryRow.supplierName)
            val entries = entryRows.map(this::entryFromRow)
            val metadata = EntityMetadata.ofEpochSeconds(
                deliveryRow.createdAtEpochSeconds,
                deliveryRow.updatedAtEpochSeconds,
            )

            Delivery(
                deliveryRow.id,
                LocalDateCodec.deserialize(deliveryRow.receivedDate).unwrapUnsafe(),
                supplier,
                deliveryRow.taxesCents,
                deliveryRow.feesCents,
                entries,
                metadata
            )
        }.distinctUntilChanged() // Only emit when data actually changes
    }

    /**
     * NOTE: Only returns the first 1000
     */
    fun getDeliveriesByReportCriteria(
        startDate: LocalDate,
        endDate: LocalDate,
        itemId: Long? = null,
        itemCategoryId: Long? = null,
        costStatus: CostStatus? = null,
        programId: Long? = null,
        purchasingAccountId: Long? = null,
        supplierId: Long? = null,
    ): Flow<List<Delivery>> {
        return database.deliveryEntryQueries.getAllByBasicReportCriteriaPaginated(
            LocalDateCodec.serialize(startDate),
            LocalDateCodec.serialize(endDate),
            itemId,
            itemCategoryId,
            costStatus?.let { CostStatusCodec.serialize(it) },
            programId,
            purchasingAccountId,
            supplierId,
            1000,
            0,
        )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.deliveryId }.map { (deliveryId, rows) ->
                    val firstRow = rows.first()

                    // TODO: Error handling
                    val received = LocalDateCodec.deserialize(firstRow.deliveryReceivedDate).unwrapUnsafe()
                    val supplier = supplierFromJoinedRow(firstRow.deliverySupplierId, firstRow.supplierName)
                    val metadata = EntityMetadata.ofEpochSeconds(
                        firstRow.deliveryCreatedAtEpochSeconds,
                        firstRow.updatedAtEpochSeconds,
                    )

                    val entries = rows.map {
                        entryFromRow(it)
                    }

                    Delivery(
                        id = deliveryId,
                        received = received,
                        supplier = supplier,
                        taxesCents = firstRow.deliveryTaxesCents,
                        feesCents = firstRow.deliveryFeesCents,
                        entries = entries,
                        metadata = metadata
                    )
                }
            }
    }

    private fun supplierFromJoinedRow(supplierId: Long, supplierName: String?): Supplier {
        require(supplierName != null) {
            "Supplier name must be defined when supplierId is defined due to foreign key constraint"
        }
        return Supplier(supplierId, supplierName)
    }

    private fun entryFromRow(row: GetByDeliveryId): Delivery.Entry {
        val item = ItemHeadline(
            row.itemId,
            row.itemName ?: throw Exception("`itemName` must be defined by foreign key constraints")
        )
        val costStatus = CostStatusCodec.deserialize(row.costStatus).unwrapUnsafe()
        val itemWeight = row.itemWeightCentigrams?.let(Weight::ofCentigrams)
        val unitWeight = row.unitWeightCentigrams.let(Weight::ofCentigrams)
        val program = row.programId?.let { id ->
            Program(id, row.programName ?: throw Exception("`programName` must be defined by foreign key constraints"))
        }
        val account = row.purchasingAccountId?.let { id ->
            Account(
                id,
                row.purchasingAccountName
                    ?: throw Exception("`purchasingAccountName` must be defined by foreign key constraints")
            )
        }
        val metadata = EntityMetadata.ofEpochSeconds(row.createdAtEpochSeconds, row.updatedAtEpochSeconds)

        return Delivery.Entry(
            row.id,
            item,
            row.unitCount,
            unitWeight,
            costStatus,
            row.unitCostCents,
            itemWeight,
            row.itemsPerUnit,
            program,
            account,
            metadata
        )
    }

    // DUMB >:(
    // TODO(?): Maybe can fix with Lens ?
    private fun entryFromRow(row: GetAllByBasicReportCriteriaPaginated): Delivery.Entry = entryFromRow(
        GetByDeliveryId(
            row.id,
            row.deliveryId,
            row.itemId,
            row.unitCount,
            row.itemWeightCentigrams,
            row.itemsPerUnit,
            row.unitWeightCentigrams,
            row.costStatus,
            row.unitCostCents,
            row.programId,
            row.purchasingAccountId,
            row.createdAtEpochSeconds,
            row.updatedAtEpochSeconds,
            row.itemName,
            row.programName,
            row.purchasingAccountName,
        )
    )

    suspend fun insertDelivery(
        received: LocalDate,
        supplierId: Long,
        taxesCents: Long?,
        feesCents: Long?,
    ): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            val id = database.deliveryQueries.insert(
                LocalDateCodec.serialize(received),
                supplierId,
                taxesCents,
                feesCents,
                now.epochSecond,
                now.epochSecond,
            ).awaitAsOne()

            println("Created delivery with id: $id")
            id
        }
    }

    suspend fun updateDelivery(
        id: Long,
        received: LocalDate,
        supplierId: Long,
        taxesCents: Long?,
        feesCents: Long?,
    ): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            val rowsUpdated = database.deliveryQueries.updateById(
                LocalDateCodec.serialize(received),
                supplierId,
                taxesCents,
                feesCents,
                now.epochSecond,
                id,
            )

            return when {
                rowsUpdated > 0 -> Result.success(id)
                else -> Result.failure(IllegalStateException("Delivery was not updated"))
            }
        }
    }

    suspend fun deleteDeliveryById(id: Long): Result<Long> {
        val rowsDeleted = database.deliveryQueries.deleteById(id)

        return when {
            rowsDeleted > 0 -> Result.success(id)
            else -> Result.failure(IllegalStateException("Delivery was not deleted"))
        }
    }

    suspend fun appendDeliveryEntry(
        deliveryId: Long,
        itemId: Long,
        unitCount: Long,
        unitWeight: Weight,
        costStatus: CostStatus,
        unitCostCents: Long,
        itemWeight: Weight? = null,
        itemsPerUnit: Long? = null,
        programId: Long? = null,
        purchasingAccountId: Long? = null,
    ): Result<Long> {
        val now = Instant.now()
        val costStatus = CostStatusCodec.serialize(costStatus)

        return Result.runCatching {
            database.deliveryEntryQueries.insert(
                deliveryId,
                itemId,
                unitCount,
                itemsPerUnit,
                itemWeight?.centigrams,
                unitWeight.centigrams,
                costStatus,
                unitCostCents,
                programId,
                purchasingAccountId,
                now.epochSecond,
                now.epochSecond,
            ).awaitAsOne()
        }
    }

    suspend fun updateDeliveryEntry(
        entryId: Long,
        itemId: Long,
        unitCount: Long,
        unitWeight: Weight,
        costStatus: CostStatus,
        unitCostCents: Long,
        itemWeight: Weight?,
        itemsPerUnit: Long?,
        programId: Long?,
        purchasingAccountId: Long?,
    ): Result<Long> {
        val now = Instant.now()
        val costStatus = CostStatusCodec.serialize(costStatus)

        return Result.runCatching {
            database.deliveryEntryQueries.updateById(
                itemId,
                unitCount,
                itemsPerUnit,
                itemWeight?.centigrams,
                unitWeight.centigrams,
                costStatus,
                unitCostCents,
                programId,
                purchasingAccountId,
                now.epochSecond,
                entryId,
            )
        }.map { rowsUpdated ->
            return when {
                rowsUpdated > 0 -> Result.success(entryId)
                else -> Result.failure(IllegalStateException("DeliveryEntry was not updated"))
            }
        }
    }

    suspend fun updateDeliveryEntryProgram(entryId: Long, programId: Long?): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            database.deliveryEntryQueries.updateProgramById(
                programId,
                now.epochSecond,
                entryId,
            )
        }.map { rowsUpdated ->
            return when {
                rowsUpdated > 0 -> Result.success(entryId)
                else -> Result.failure(IllegalStateException("DeliveryEntry was not updated"))
            }
        }
    }

    suspend fun updateDeliveryEntryPurchasingAccount(entryId: Long, purchasingAccountId: Long?): Result<Long> {
        val now = Instant.now()

        return Result.runCatching {
            database.deliveryEntryQueries.updatePurchasingAccountById(
                purchasingAccountId,
                now.epochSecond,
                entryId,
            )
        }.map { rowsUpdated ->
            return when {
                rowsUpdated > 0 -> Result.success(entryId)
                else -> Result.failure(IllegalStateException("DeliveryEntry was not updated"))
            }
        }
    }

    suspend fun deleteDeliveryEntryById(id: Long): Result<Long> {
        val rowsDeleted = database.deliveryEntryQueries.deleteById(id)

        return when {
            rowsDeleted > 0 -> Result.success(id)
            else -> Result.failure(IllegalStateException("DeliveryEntry was not deleted"))
        }
    }
}
