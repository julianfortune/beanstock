package com.julianfortune.beanstock.data.repository

import com.julianfortune.beanstock.data.model.Account
import com.julianfortune.beanstock.db.Database

fun accountRepositoryOf(database: Database) = GenericNamedEntityRepository(
    entityName = "PurchasingAccount",
    getAllFn = database.purchasingAccountQueries::getAll,
    insertFn = database.purchasingAccountQueries::insert,
    updateFn = database.purchasingAccountQueries::updateById,
    deleteFn = database.purchasingAccountQueries::deleteById,
    fromRow = { row -> Account(row.id, row.name) }
)
