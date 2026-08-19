package com.julianfortune.beanstock.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.nio.file.Path
import java.util.*

actual class DatabaseDriverFactory(val databaseDirectory: Path) {

    companion object {
        const val FILE_NAME = "default.db"
    }

    actual suspend fun createDriver(): SqlDriver {
        val databasePath = databaseDirectory.resolve(FILE_NAME)
        println("Using database at: '$databasePath'")

        // NOTE: `foreign_keys` are disabled by default
        val properties = Properties().apply { put("foreign_keys", "true") }

        val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath}", properties).also {
            Database.Schema.create(it).await()
        }

        return driver
    }

}
