package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.beanstock.createTestDatabase
import com.julianfortune.beanstock.data.model.CostStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate
import kotlin.properties.Delegates.notNull


class ReportRepositoryTest {

    val database = runBlocking { createTestDatabase() }
    val reportRepository = ReportRepository(database)

    @Test
    fun getById() {
        // WHEN
        val result = runBlocking {
            reportRepository.getById(1L).firstOrNull()
        }

        // THEN
        assertNull(result)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "true,  true,  true,  true,  true,  true ", // All present
            "true,  true,  false, true,   true,  true", // Most present
            "false, true,  false, false,  true,  true", // Some present
            "true,  false, false, false, false, false", // Item only
            "false, false, false, false, false, true ", // Supplier only
            "false, false, false, false, false, false", // All nulls
        ]
    )
    fun insertBasicReport(
        hasItem: Boolean,
        hasCategory: Boolean,
        hasCostStatus: Boolean,
        hasProgram: Boolean,
        hasAccount: Boolean,
        hasSupplier: Boolean
    ) {
        // GIVEN
        val start = LocalDate.of(2026, 1, 1)
        val end = LocalDate.of(2026, 3, 31)
        val name = "Q1 2026 Report"

        val itemId = if (hasItem) {
            runBlocking { database.itemQueries.insert("Test Item", null).awaitAsOne() }
        } else null

        val itemCategoryId = if (hasCategory) {
            runBlocking { database.categoryQueries.insert("Test Category").awaitAsOne() }
        } else null

        val costStatus = if (hasCostStatus) CostStatus.PURCHASED else null

        val programId = if (hasProgram) {
            runBlocking { database.programQueries.insert("Test Program").awaitAsOne() }
        } else null

        val purchasingAccountId = if (hasAccount) {
            runBlocking { database.purchasingAccountQueries.insert("Test Account").awaitAsOne() }
        } else null

        val supplierId = if (hasSupplier) {
            runBlocking { database.supplierQueries.insert("Test Supplier").awaitAsOne() }
        } else null

        // WHEN
        val id = runBlocking {
            reportRepository.insert(
                name = name,
                start = start,
                end = end,
                itemId = itemId,
                itemCategoryId = itemCategoryId,
                costStatus = costStatus,
                programId = programId,
                purchasingAccountId = purchasingAccountId,
                supplierId = supplierId
            ).getOrThrow()
        }

        // THEN
        val storedReport = runBlocking { reportRepository.getById(id).first() }

        assertThat(storedReport).isNotNull()
        assertThat(storedReport!!.id).isEqualTo(id)
        assertThat(storedReport.name).isEqualTo(name)

        // Verify foreign keys directly against our variables (handles both null and non-null cases)
        assertThat(storedReport.criteria.item?.id).isEqualTo(itemId)
        assertThat(storedReport.criteria.category?.id).isEqualTo(itemCategoryId)
        assertThat(storedReport.criteria.costStatus).isEqualTo(costStatus)
        assertThat(storedReport.criteria.program?.id).isEqualTo(programId)
        assertThat(storedReport.criteria.account?.id).isEqualTo(purchasingAccountId)
        assertThat(storedReport.criteria.supplier?.id).isEqualTo(supplierId)
    }

    @Nested
    inner class WithExistingRecord {
        private var reportId by notNull<Long>()

        val initialName = "Initial Report"
        val initialStart: LocalDate = LocalDate.of(2026, 5, 1)
        val initialEnd: LocalDate = LocalDate.of(2026, 5, 31)

        @BeforeEach
        fun setUp() {
            reportId = runBlocking {
                reportRepository.insert(
                    name = initialName,
                    start = initialStart,
                    end = initialEnd,
                    itemId = null,
                    itemCategoryId = null,
                    costStatus = null,
                    programId = null,
                    purchasingAccountId = null,
                    supplierId = null
                ).getOrThrow()
            }
        }

        @Test
        fun updateBasicReport() {
            // GIVEN
            val updatedName = "Updated Report Name"
            val updatedStart = LocalDate.of(2026, 6, 1)
            val updatedEnd = LocalDate.of(2026, 6, 30)

            // WHEN
            val id = runBlocking {
                reportRepository.update(
                    id = reportId,
                    name = updatedName,
                    start = updatedStart,
                    end = updatedEnd,
                    itemId = null,
                    itemCategoryId = null,
                    costStatus = CostStatus.PURCHASED,
                    programId = null,
                    purchasingAccountId = null,
                    supplierId = null
                ).getOrThrow()
            }

            // THEN
            val storedReport = runBlocking { reportRepository.getById(id).first() }

            assertThat(storedReport).isNotNull()
            assertThat(storedReport!!.name).isEqualTo(updatedName)
            assertThat(storedReport.criteria.costStatus).isEqualTo(CostStatus.PURCHASED)
        }

        @Test
        fun getOneById() {
            // WHEN
            val result = runBlocking {
                reportRepository.getById(reportId).first()
            }

            // THEN
            assertThat(result).isNotNull()
            assertThat(result!!.id).isEqualTo(reportId)
            assertThat(result.name).isEqualTo(initialName)
            assertThat(result.criteria.start).isEqualTo(initialStart)
            assertThat(result.criteria.end).isEqualTo(initialEnd)
        }

        @Test
        fun deleteBasicReport() {
            // WHEN
            val id = runBlocking {
                reportRepository.delete(reportId).getOrThrow()
            }

            // THEN
            val result = runBlocking {
                reportRepository.getById(id).firstOrNull()
            }
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class WithMultipleRecords {

        fun getName(index: Int) = "Batch Report $index"
        fun getStartDate(index: Int): LocalDate = LocalDate.of(2026, index, 1)
        fun getEndDate(index: Int): LocalDate = LocalDate.of(2026, index, 28)

        @BeforeEach
        fun setUp() {
            runBlocking {
                (1..3).forEach { index ->
                    reportRepository.insert(
                        name = getName(index),
                        start = getStartDate(index),
                        end = getEndDate(index),
                        itemId = null,
                        itemCategoryId = null,
                        costStatus = null,
                        programId = null,
                        purchasingAccountId = null,
                        supplierId = null
                    ).getOrThrow()
                }
            }
        }

        @Test
        fun getAllAsHeadlines() {
            // WHEN
            val headlines = runBlocking {
                reportRepository.getAllAsHeadlines().first()
            }

            // THEN
            headlines.forEachIndexed { index, headline ->
                val position = index + 1

                assertThat(headline.id).isNotNull()
                assertThat(headline.name).isEqualTo(getName(position))
                assertThat(headline.start).isEqualTo(getStartDate(position))
                assertThat(headline.end).isEqualTo(getEndDate(position))
            }
        }
    }
}
