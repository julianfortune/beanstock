package com.julianfortune.beanstock.data.repository

import com.julianfortune.beanstock.data.model.Program
import com.julianfortune.beanstock.db.Database

fun programRepositoryOf(database: Database) = GenericNamedEntityRepository(
    entityName = "Program",
    getAllFn = database.programQueries::getAll,
    insertFn = database.programQueries::insert,
    updateFn = database.programQueries::updateById,
    deleteFn = database.programQueries::deleteById,
    fromRow = { row -> Program(row.id, row.name) }
)
