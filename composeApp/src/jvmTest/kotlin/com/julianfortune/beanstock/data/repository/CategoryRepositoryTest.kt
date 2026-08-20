package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.julianfortune.beanstock.createTestDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.properties.Delegates

class CategoryRepositoryTest {

    val database = runBlocking { createTestDatabase() }
    val repository = categoryRepositoryOf(database)

    @Nested
    inner class GivenACategory {

        private var categoryId by Delegates.notNull<Long>()

        @BeforeEach
        fun setUp() {
            categoryId = runBlocking {
                database.categoryQueries.insert("Example").awaitAsOne()
            }
        }

        @Nested
        inner class WithAReferencingReport {

            private var reportId by Delegates.notNull<Long>()

            @BeforeEach
            fun setUp() {
                reportId = runBlocking {
                    database.basicReportQueries.insert(
                        "Example Report",
                        "2026-01-01",
                        "2026-01-31",
                        null,
                        categoryId,
                        null,
                        null,
                        null,
                        null,
                        Instant.now().epochSecond,
                        Instant.now().epochSecond,
                    ).awaitAsOne()
                }

                runBlocking {
                    val report = database.basicReportQueries.getById(reportId).awaitAsOne()
                    println(report)
                }
            }

            @Test
            fun deletingCategoryFails() {
                // WHEN
                val result = runBlocking { repository.deleteById(categoryId) }

                // THEN
                Assertions.assertThat(result.isFailure).isTrue()
            }
        }
    }
}