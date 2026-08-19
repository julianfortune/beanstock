package com.julianfortune.beanstock

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.julianfortune.beanstock.db.Database
import java.util.*

suspend fun createTestDatabase(): Database {
    val properties = Properties().apply { put("foreign_keys", "true") }

    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, properties).also {
        Database.Schema.create(it).await()
    }

    return Database(driver)
}
