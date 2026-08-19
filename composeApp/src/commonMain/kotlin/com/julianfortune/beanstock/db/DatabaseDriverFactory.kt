package com.julianfortune.beanstock.db

import app.cash.sqldelight.db.SqlDriver

// Based on https://sqldelight.github.io/sqldelight/2.1.0/multiplatform_sqlite/ and
expect class DatabaseDriverFactory {
    suspend fun createDriver(): SqlDriver
}
