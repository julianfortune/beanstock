package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.beanstock.createTestDatabase
import com.julianfortune.beanstock.data.codec.LocalDateCodec
import com.julianfortune.beanstock.data.model.*
import com.julianfortune.beanstock.db.Database
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import kotlin.properties.Delegates

class DeliveryRepositoryTest {

    val database: Database = runBlocking { createTestDatabase() }
    val repository: DeliveryRepository = DeliveryRepository(database)

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        val supplierId = database.supplierQueries.insert("Supplier 1").awaitAsOne()

        // Add some initial data to help prevent the id being 1 ( the number of rows mutated during most operations)
        (1..5).forEach {
            repository.insertDelivery(
                LocalDate.of(2025, 1, it),
                supplierId,
                null,
                null,
            )
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            " , ",
            " , 500",
            "500 , ",
            "500 , 500",
        ]
    )
    fun insertDelivery(taxesCents: Long?, feesCents: Long?) {
        // GIVEN
        val supplierId = runBlocking {
            database.supplierQueries.insert("Foo Bar").awaitAsOne()
        }
        val received = LocalDate.of(2025, 10, 26)

        // WHEN
        val id = runBlocking {
            repository.insertDelivery(
                received,
                supplierId,
                taxesCents,
                feesCents,
            ).getOrThrow()
        }

        // THEN
        val storedDelivery = database.deliveryQueries.getById(id).executeAsOne()
        assertThat(storedDelivery.receivedDate).isEqualTo(LocalDateCodec.serialize(received))
        assertThat(storedDelivery.supplierId).isEqualTo(supplierId)
        assertThat(storedDelivery.taxesCents).isEqualTo(taxesCents)
        assertThat(storedDelivery.feesCents).isEqualTo(feesCents)
    }

    @Nested
    inner class WithDelivery {
        private var deliveryId by Delegates.notNull<Long>()

        val initialSupplierName = "Supplier ABC"

        private var initialSupplierId by Delegates.notNull<Long>()

        @BeforeEach
        fun setUp() {
            initialSupplierId = runBlocking {
                database.supplierQueries.insert(initialSupplierName).awaitAsOne()
            }
            val received = LocalDate.of(2025, 10, 26)

            deliveryId = runBlocking {
                repository.insertDelivery(
                    received,
                    initialSupplierId,
                    null,
                    null,
                ).getOrThrow()
            }
        }

        @Test
        fun getDeliveryById() {
            // WHEN
            val result = runBlocking {
                repository.getDeliveryById(deliveryId).first()
            }

            // THEN
            println(result)

            assertThat(result).isNotNull()

        }

        @ParameterizedTest
        @CsvSource(
            value = [
                " , ",
                " , 500",
                "500 , ",
                "500 , 500",
            ]
        )
        fun updateDelivery(taxesCents: Long?, feesCents: Long?) {
            // GIVEN
            val supplierId = runBlocking {
                database.supplierQueries.insert("Bizz Bazz").awaitAsOne()
            }
            val received = LocalDate.of(2026, 4, 8)

            // WHEN
            val id = runBlocking {
                repository.updateDelivery(
                    deliveryId,
                    received,
                    supplierId,
                    taxesCents,
                    feesCents,
                ).getOrThrow()
            }

            // THEN
            val storedDelivery = database.deliveryQueries.getById(id).executeAsOne()
            assertThat(storedDelivery.receivedDate).isEqualTo(LocalDateCodec.serialize(received))
            assertThat(storedDelivery.supplierId).isEqualTo(supplierId)
            assertThat(storedDelivery.taxesCents).isEqualTo(taxesCents)
            assertThat(storedDelivery.feesCents).isEqualTo(feesCents)
        }

        @Test
        fun deleteDelivery() {
            // WHEN
            val id = runBlocking {
                repository.deleteDeliveryById(deliveryId).getOrThrow()
            }

            // THEN
            val storedDelivery = database.deliveryQueries.getById(id).executeAsOneOrNull()
            assertThat(storedDelivery).isNull()
        }

        @ParameterizedTest
        @CsvSource(
            value = [
                "3, 6700, 'PURCHASED', 800, , , , ",
                "3, 6700, 'NO_COST', 0, , , , ",
            ],
        )
        fun insertDeliveryEntry(
            unitCount: Long,
            unitWeightCentigrams: Long,
            costStatusString: String,
            unitCostCents: Long,
            itemWeightCentigrams: Long?,
            itemsPerUnit: Long?,
            programId: Long?,
            purchasingAccountId: Long?
        ) {
            // GIVEN
            val itemId = runBlocking {
                database.itemQueries.insert("Food", null).awaitAsOne()
            }

            // Map the primitive CSV primitives back to your domain types
            val expectedUnitWeight = Weight(unitWeightCentigrams)
            val costStatus = CostStatus.valueOf(costStatusString)
            val expectedItemWeight = itemWeightCentigrams?.let { Weight(it) }

            // WHEN
            val entryId = runBlocking {
                repository.appendDeliveryEntry(
                    deliveryId = deliveryId,
                    itemId = itemId,
                    unitCount = unitCount,
                    unitWeight = expectedUnitWeight,
                    costStatus = costStatus,
                    unitCostCents = unitCostCents,
                    itemWeight = expectedItemWeight,
                    itemsPerUnit = itemsPerUnit,
                    programId = programId,
                    purchasingAccountId = purchasingAccountId
                ).getOrThrow()
            }

            // THEN
            val storedEntry = database.deliveryEntryQueries.getById(entryId).executeAsOne()

            assertThat(storedEntry.id).isEqualTo(entryId)
            assertThat(storedEntry.deliveryId).isEqualTo(deliveryId)
            assertThat(storedEntry.itemId).isEqualTo(itemId)
            assertThat(storedEntry.unitCount).isEqualTo(unitCount)
            assertThat(storedEntry.unitWeightCentigrams).isEqualTo(expectedUnitWeight.centigrams)
            assertThat(storedEntry.costStatus).isEqualTo(costStatusString)
            assertThat(storedEntry.unitCostCents).isEqualTo(unitCostCents)
            assertThat(storedEntry.itemWeightCentigrams).isEqualTo(expectedItemWeight?.centigrams)
            assertThat(storedEntry.itemsPerUnit).isEqualTo(itemsPerUnit)
            assertThat(storedEntry.programId).isEqualTo(programId)
            assertThat(storedEntry.purchasingAccountId).isEqualTo(purchasingAccountId)
        }

        @Nested
        inner class WithDeliveryEntry {
            private var entryId by Delegates.notNull<Long>()

            val initialItemName = "Initial Food"
            val initialProgramName = "Program A"
            val initialPurchasingAccountName = "Account I"

            private var initialItemId by Delegates.notNull<Long>()
            private var initialProgramId by Delegates.notNull<Long>()
            private var initialPurchasingAccountId by Delegates.notNull<Long>()

            @BeforeEach
            fun setUp() {
                initialItemId = runBlocking {
                    database.itemQueries.insert(initialItemName, null).awaitAsOne()
                }
                initialProgramId = runBlocking {
                    database.programQueries.insert(initialProgramName).awaitAsOne()
                }
                initialPurchasingAccountId = runBlocking {
                    database.purchasingAccountQueries.insert(initialPurchasingAccountName).awaitAsOne()
                }

                entryId = runBlocking {
                    repository.appendDeliveryEntry(
                        deliveryId = deliveryId,
                        itemId = initialItemId,
                        unitCount = 1,
                        unitWeight = Weight(1000),
                        costStatus = CostStatus.PURCHASED,
                        unitCostCents = 100,
                        itemWeight = null,
                        itemsPerUnit = null,
                        programId = initialProgramId,
                        purchasingAccountId = initialPurchasingAccountId
                    ).getOrThrow()
                }
            }

            @Test
            fun getDeliveryById() {
                // WHEN
                val result = runBlocking {
                    repository.getDeliveryById(deliveryId).first()
                }

                // THEN
                val firstEntry = result!!.entries.first()

                assertThat(firstEntry.item).isEqualTo(ItemHeadline(initialItemId, initialItemName))
                assertThat(firstEntry.program).isEqualTo(Program(initialProgramId, initialProgramName))
                assertThat(firstEntry.account).isEqualTo(
                    Account(
                        initialPurchasingAccountId,
                        initialPurchasingAccountName
                    )
                )
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "5, 12000, 'NO_COST', 0",
                    "10, 500, 'PURCHASED', 850",
                ],
            )
            fun updateDeliveryEntry(
                unitCount: Long,
                unitWeightCentigrams: Long,
                costStatusString: String,
                unitCostCents: Long,
            ) {
                // GIVEN
                val newItemId = runBlocking {
                    database.itemQueries.insert("Updated Food", null).awaitAsOne()
                }
                val expectedUnitWeight = Weight(unitWeightCentigrams)
                val costStatus = CostStatus.valueOf(costStatusString)

                // WHEN
                val id = runBlocking {
                    repository.updateDeliveryEntry(
                        entryId = entryId,
                        itemId = newItemId,
                        unitCount = unitCount,
                        unitWeight = expectedUnitWeight,
                        costStatus = costStatus,
                        unitCostCents = unitCostCents,
                        itemWeight = null,
                        itemsPerUnit = null,
                        programId = null,
                        purchasingAccountId = null
                    ).getOrThrow()
                }

                // THEN
                val storedEntry = database.deliveryEntryQueries.getById(id).executeAsOne()
                assertThat(storedEntry.itemId).isEqualTo(newItemId)
                assertThat(storedEntry.unitCount).isEqualTo(unitCount)
                assertThat(storedEntry.unitWeightCentigrams).isEqualTo(expectedUnitWeight.centigrams)
                assertThat(storedEntry.costStatus).isEqualTo(costStatusString)
                assertThat(storedEntry.unitCostCents).isEqualTo(unitCostCents)
                assertThat(storedEntry.itemWeightCentigrams).isNull()
                assertThat(storedEntry.itemsPerUnit).isNull()
                assertThat(storedEntry.programId).isNull()
                assertThat(storedEntry.purchasingAccountId).isNull()
            }

            @Test
            fun deleteDeliveryEntry() {
                // WHEN
                val id = runBlocking {
                    repository.deleteDeliveryEntryById(entryId).getOrThrow()
                }

                // THEN
                val storedEntry = database.deliveryEntryQueries.getById(id).executeAsOneOrNull()
                assertThat(storedEntry).isNull()
            }
        }
    }

}
