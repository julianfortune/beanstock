package com.julianfortune.beanstock.data.repository

import com.julianfortune.beanstock.data.model.Supplier
import com.julianfortune.beanstock.db.Database


fun supplierRepositoryOf(database: Database) = GenericNamedEntityRepository(
    entityName = "Supplier",
    getAllFn = database.supplierQueries::getAll,
    insertFn = database.supplierQueries::insert,
    updateFn = database.supplierQueries::updateById,
    deleteFn = database.supplierQueries::deleteById,
    fromRow = { row -> Supplier(row.id, row.name) }
)
