package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import com.julianfortune.beanstock.createTestDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import com.julianfortune.beanstock.db.Supplier as DbSupplier

class SupplierRepositoryTest {

    val database = runBlocking { createTestDatabase() }

    val repository = SupplierRepository(database)

    @Test
    fun insert(): Unit = runBlocking {
        // GIVEN
        val supplierName = "FoodSource LLC"

        // WHEN
        val id = repository.insert(supplierName).getOrThrow()

        // THEN
        val rows = database.supplierQueries.getAll().awaitAsList()
        assertThat(rows).containsExactly(DbSupplier(id, supplierName))
    }

    @Test
    fun update(): Unit = runBlocking {
        // GIVEN
        val id = repository.insert("FoodSource LLC").getOrThrow()
        val updatedSupplierName = "FoodSource, LLC."

        // WHEN
        repository.update(id, updatedSupplierName)

        // THEN
        val rows = database.supplierQueries.getAll().awaitAsList()
        assertThat(rows).containsExactly(DbSupplier(id, updatedSupplierName))
    }

    @Test
    fun delete(): Unit = runBlocking {
        // GIVEN
        val id = repository.insert("FoodSource LLC").getOrThrow()

        // WHEN
        val result = repository.delete(id).getOrThrow()

        // THEN
        assertThat(result).isEqualTo(id)

        val row = database.supplierQueries.getById(id).awaitAsOneOrNull()
        assertThat(row).isNull()
    }

}
