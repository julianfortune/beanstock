package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.beanstock.core.util.unwrapUnsafe
import com.julianfortune.beanstock.createTestDatabase
import com.julianfortune.beanstock.data.codec.CostStatusCodec
import com.julianfortune.beanstock.data.codec.LocalDateCodec
import com.julianfortune.beanstock.data.model.CostStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Instant
import java.time.LocalDate

class ReportResultRepositoryTest {

    companion object {
        const val KG: Long = 1000L * 100L
        const val DOLLARS: Long = 100L
    }

    val database = runBlocking { createTestDatabase() }

    val repository = ReportResultRepository(database)

    var supplierIds = emptyMap<String, Long>()
    var programIds = emptyMap<String, Long>()
    var accountIds = emptyMap<String, Long>()
    var categoryIds = emptyMap<String, Long>()
    var itemIds = emptyMap<String, Long>()

    var deliveryIds = emptyMap<String, Long>()
    var entryIds = emptyMap<String, Long>()


    private suspend fun insertItem(name: String, category: String): Long {
        val id = database.itemQueries.insert(name, null).awaitAsOne()
        database.itemCategoryQueries.insert(id, categoryIds[category]!!)

        return id
    }

    private suspend fun insertDelivery(
        receivedDate: String,
        supplierId: Long,
        taxesCents: Long? = null,
        feesCents: Long? = null,
    ): Long {
        return database.deliveryQueries.insert(
            receivedDate,
            supplierId,
            taxesCents,
            feesCents,
            Instant.now().epochSecond,
            Instant.now().epochSecond,
        ).awaitAsOne()
    }

    private suspend fun insertEntry(
        deliveryName: String,
        itemName: String,
        programName: String?,
        accountName: String?,
        unitCount: Long,
        unitWeightKg: Long,
        costStatus: CostStatus,
        unitCostDollars: Long,
    ): Long {
        return database.deliveryEntryQueries.insert(
            deliveryIds[deliveryName]!!,
            itemIds[itemName]!!,
            unitCount,
            itemsPerUnit = null,
            itemWeightCentigrams = null,
            unitWeightCentigrams = unitWeightKg * KG,
            costStatus = CostStatusCodec.serialize(costStatus),
            unitCostCents = unitCostDollars * DOLLARS,
            programId = programName?.let { programIds[it]!! },
            purchasingAccountId = accountName?.let { accountIds[it]!! },
            Instant.now().epochSecond,
            Instant.now().epochSecond,
        ).awaitAsOne()
    }

    private suspend fun insertSuppliers() = mapOf(
        "ABC Foods" to database.supplierQueries.insert("ABC Foods").awaitAsOne(),
        "Harvest Produce" to database.supplierQueries.insert("Harvest Produce").awaitAsOne(),
    )

    private suspend fun insertCategories() = mapOf(
        "Bulk" to database.categoryQueries.insert("Bulk").awaitAsOne(),
        "Canned" to database.categoryQueries.insert("Canned").awaitAsOne(),
        "Dairy" to database.categoryQueries.insert("Dairy").awaitAsOne(),
        "Frozen" to database.categoryQueries.insert("Frozen").awaitAsOne(),
        "Produce" to database.categoryQueries.insert("Produce").awaitAsOne(),
    )

    private suspend fun insertItems() = mapOf(
        "Flour" to insertItem("Flour", "Bulk"),
        "Oats" to insertItem("Oats", "Bulk"),
        "Pasta" to insertItem("Pasta", "Bulk"),
        "Rice" to insertItem("Rice", "Bulk"),
        "Sugar" to insertItem("Sugar", "Bulk"),

        "Chili" to insertItem("Chili", "Canned"),
        "Chickpeas" to insertItem("Chickpeas", "Canned"),
        "Peaches" to insertItem("Peaches", "Canned"),
        "Soup" to insertItem("Soup", "Canned"),
        "Tuna" to insertItem("Tuna", "Canned"),

        "Butter" to insertItem("Butter", "Dairy"),
        "Cheese" to insertItem("Cheese", "Dairy"),
        "Cream" to insertItem("Cream", "Dairy"),
        "Milk" to insertItem("Milk", "Dairy"),
        "Yogurt" to insertItem("Yogurt", "Dairy"),

        "Berries" to insertItem("Berries", "Frozen"),
        "Fries" to insertItem("Fries", "Frozen"),
        "Peas" to insertItem("Peas", "Frozen"),
        "Sorbet" to insertItem("Sorbet", "Frozen"),
        "Pizza" to insertItem("Pizza", "Frozen"),

        "Apple" to insertItem("Apple", "Produce"),
        "Banana" to insertItem("Banana", "Produce"),
        "Carrot" to insertItem("Carrot", "Produce"),
        "Onion" to insertItem("Onion", "Produce"),
        "Potato" to insertItem("Potato", "Produce"),
    )

    private suspend fun insertPrograms() = mapOf(
        "Program A" to database.programQueries.insert("Program A").awaitAsOne(),
        "Program B" to database.programQueries.insert("Program B").awaitAsOne(),
    )

    private suspend fun insertAccounts() = mapOf(
        "Account I" to database.purchasingAccountQueries.insert("Account I").awaitAsOne(),
        "Account II" to database.purchasingAccountQueries.insert("Account II").awaitAsOne(),
    )

    private suspend fun insertDeliveries() = mapOf(
        "2025-01-01" to insertDelivery("2025-01-01", supplierIds["ABC Foods"]!!, 150, 500),
        "2025-02-08" to insertDelivery("2025-02-08", supplierIds["Harvest Produce"]!!, 30, 80),
        "2025-03-01" to insertDelivery("2025-03-01", supplierIds["ABC Foods"]!!),
        "2025-04-07" to insertDelivery("2025-04-07", supplierIds["Harvest Produce"]!!, 1000, null),
        "2025-05-01" to insertDelivery("2025-05-01", supplierIds["ABC Foods"]!!, null, 5),
        "2025-06-06" to insertDelivery("2025-06-06", supplierIds["Harvest Produce"]!!),
        "2025-07-01" to insertDelivery("2025-07-01", supplierIds["ABC Foods"]!!),
        "2025-08-08" to insertDelivery("2025-08-08", supplierIds["Harvest Produce"]!!),
        "2025-09-29" to insertDelivery("2025-09-29", supplierIds["ABC Foods"]!!),
        "2025-10-02" to insertDelivery("2025-10-02", supplierIds["Harvest Produce"]!!),
        "2025-11-01" to insertDelivery("2025-11-01", supplierIds["ABC Foods"]!!),
        "2025-12-01" to insertDelivery("2025-12-01", supplierIds["Harvest Produce"]!!),
    )

    // NOTE: (!) If adding more entries do one at a time and manually do the math
    private suspend fun insertEntries() = mapOf(
        // January delivery
        "c8d75c82f2ff" to insertEntry("2025-01-01", "Flour", null, "Account II", 5, 200, CostStatus.PURCHASED, 80),
        "eeaa337f5ea2" to insertEntry("2025-01-01", "Cheese", null, "Account II", 1, 10, CostStatus.PURCHASED, 15),
        "81dd58eb2425" to insertEntry("2025-01-01", "Banana", "Program A", "Account I", 3, 50, CostStatus.PURCHASED, 20),
        // Feb delivery
        "900a9e8ad945" to insertEntry("2025-02-08", "Potato", "Program B", null, 6, 100, CostStatus.NO_COST, 0),
        "c711839f3549" to insertEntry("2025-02-08", "Carrot", "Program B", null, 2, 50, CostStatus.NO_COST, 0),
        // March delivery (ABC Foods)
        "d4994631187e" to insertEntry("2025-03-01", "Pizza", "Program A", "Account I", 1, 10, CostStatus.PURCHASED, 10),
        "6826b0878faa" to insertEntry("2025-03-01", "Sorbet", "Program A", "Account I", 2, 10, CostStatus.PURCHASED, 20),
        // April delivery (Harvest Produce)
        "35e140913860" to insertEntry("2025-04-07", "Milk", "Program B", "Account II", 2, 100, CostStatus.PURCHASED, 40),
        "0502a4a92cb1" to insertEntry("2025-04-07", "Yogurt", "Program B", "Account II", 2, 100, CostStatus.PURCHASED, 40),
        "5f0152bd7f31" to insertEntry("2025-04-07", "Onion", "Program B", null, 3, 10, CostStatus.NO_COST, 0),
        // May delivery (ABC Foods) 2025-05-01
        "5495978b8e85" to insertEntry("2025-05-01", "Peaches", "Program A", "Account II", 1, 40, CostStatus.PURCHASED, 20),
//        "0495f7022a60" to insertEntry("2025-05-01", "Tuna", "Program A", "Account II", 2, 30, CostStatus.PURCHASED, 10),
        // June delivery (Harvest Produce) 2025-06-06
//        "b35d39f127a7" to insertEntry("2025-06-06", "Pasta", "Program B", "Account I", 10, 10, CostStatus.PURCHASED, 5),
//        "4dfa63c873a3" to insertEntry("2025-06-06", "Chickpeas", "Program A", "Account I", 2, 30, CostStatus.PURCHASED, 10),
        // July delivery (ABC Foods) 2025-07-01
//        "75d455221f9f" to insertEntry("2025-07-01", "Berries", null, null, 1, 10, CostStatus.PURCHASED, 100),
//        "65bf19f7b3db" to insertEntry("2025-07-01", "Oats", null, "Account I", 2, 5, CostStatus.PURCHASED, 10),
        // August delivery (Harvest Produce) 2025-08-08
        "9b40663aee6e" to insertEntry("2025-08-08", "Apple", "Program A", "Account I", 8, 20, CostStatus.PURCHASED, 15),
        // Sep delivery (ABC Foods) 2025-09-29
//        "69fcbea5ae72" to insertEntry("2025-09-29", "Rice", "Program B", "Account I", 1, 10, CostStatus.PURCHASED, 10),
        // October delivery (Harvest Produce) 2025-10-02
//        "6706d8cb7f8e" to insertEntry("2025-10-02", "Butter", "Program A", "Account II", 2, 10, CostStatus.PURCHASED, 30),
        // November delivery (ABC Foods) 2025-11-01
//        "7153902e74de" to insertEntry("2025-11-01", "Potato", "Program B", null, 1, 10, CostStatus.NO_COST, 10),
        // December delivery (Harvest Produce) 2025-12-01
        "dc6d1dc67934" to insertEntry("2025-12-01", "Peas", "Program A", "Account II", 6, 10, CostStatus.PURCHASED, 15), // 60, 90
    )

    @BeforeEach
    fun setUp() = runBlocking {
        supplierIds = insertSuppliers()
        programIds = insertPrograms()
        accountIds = insertAccounts()

        // Items
        categoryIds = insertCategories()
        itemIds = insertItems()

        // Deliveries
        deliveryIds = insertDeliveries()
        entryIds = insertEntries()
    }

    @Test
    fun full() {
        // GIVEN (above)
        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
            ).first()
        }

        // THEN
        val expectedEntryCount = 13
        val expectedTotalWeightKg = 2580
        val expectedTotalCostDollars = 915
        val expectedTaxesCents = 1180L
        val expectedFeesCents = 585L

        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "2025-01-01, 2025-01-01, 3, 1160, 475, 150, 500", // One day
            "2025-01-01, 2025-01-31, 3, 1160, 475, 150, 500", // Month
            "2025-11-02, 2026-01-01, 1,   60,  90, 0, 0", // Bounds checking
        ]
    )
    fun forDateRange(
        startString: String,
        endString: String,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val startDate = LocalDateCodec.deserialize(startString).unwrapUnsafe()
        val endDate = LocalDateCodec.deserialize(endString).unwrapUnsafe()

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                startDate,
                endDate,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Flour,  1, 1000, 400,  150, 500",
            "Potato, 1,  600,   0,   30,  80",
            "Milk,   1,  200,  80, 1000,   0",
        ]
    )
    fun forItemId(
        itemName: String,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val itemId = itemIds[itemName]!!

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                itemId = itemId,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Bulk,    1, 1000,  400,  150, 500",
            "Canned,  1,   40,   20,    0,   5",
            "Dairy,   3,  410,  175, 1150, 500",
            "Frozen,  3,   90,  140,    0,   0",
            "Produce, 5, 1040,  180, 1180, 580",
        ]
    )
    fun forCategoryId(
        categoryName: String,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val categoryId = categoryIds[categoryName]!!

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                itemCategoryId = categoryId,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "PURCHASED, 10, 1850, 915, 1150, 505",
            "NO_COST,    3,  730,   0, 1030,  80",
        ]
    )
    fun forCostStatus(
        costStatus: CostStatus,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                costStatus = costStatus,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Program A, 6,  440,  340,  150, 505",
            "Program B, 5, 1130,  160, 1030,  80",
        ]
    )
    fun forProgramId(
        programName: String,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val programId = programIds[programName]!!

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                programId = programId,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Account I,  4,  340,  230,  150, 500",
            "Account II, 6, 1510,  685, 1150, 505",
        ]
    )
    fun forPurchasingAccountId(
        accountName: String,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val accountId = accountIds[accountName]!!

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                purchasingAccountId = accountId,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "ABC Foods,       6, 1230,  545,  150, 505",
            "Harvest Produce, 7, 1350,  370, 1030,  80",
        ]
    )
    fun forSupplierId(
        supplierName: String,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val supplierId = supplierIds[supplierName]!!

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                supplierId = supplierId,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Apple, Produce, Harvest Produce, Program A, Account I, PURCHASED, 1, 160, 120, 0, 0"
        ]
    )
    fun withAllCriteria(
        itemName: String,
        categoryName: String,
        supplierName: String,
        programName: String,
        accountName: String,
        costStatus: CostStatus,
        expectedEntryCount: Int,
        expectedTotalWeightKg: Long,
        expectedTotalCostDollars: Long,
        expectedTaxesCents: Long,
        expectedFeesCents: Long,
    ) {
        // GIVEN
        val itemId = itemIds[itemName]!!
        val categoryId = categoryIds[categoryName]!!
        val supplierId = supplierIds[supplierName]!!
        val accountId = accountIds[accountName]!!
        val programId = programIds[programName]!!

        // WHEN
        val result = runBlocking {
            repository.getResultsForBasicReportCriteria(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2026, 1, 1),
                itemId = itemId,
                itemCategoryId = categoryId,
                supplierId = supplierId,
                programId = programId,
                purchasingAccountId = accountId,
                costStatus = costStatus,
            ).first()
        }

        // THEN
        assertThat(result!!).isNotNull
        assertThat(result.entryCount).isEqualTo(expectedEntryCount)
        assertThat(result.totalWeight.centigrams).isEqualTo(expectedTotalWeightKg * KG)
        assertThat(result.totalCostCents).isEqualTo(expectedTotalCostDollars * DOLLARS)
        assertThat(result.totalDeliveryTaxesCents).isEqualTo(expectedTaxesCents)
        assertThat(result.totalDeliveryFeesCents).isEqualTo(expectedFeesCents)
    }
}
