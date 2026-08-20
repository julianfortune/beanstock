package com.julianfortune.beanstock.data.repository

import com.julianfortune.beanstock.data.model.Category
import com.julianfortune.beanstock.db.Database


fun categoryRepositoryOf(database: Database) = GenericNamedEntityRepository(
    entityName = "Category",
    getAllFn = database.categoryQueries::getAll,
    insertFn = database.categoryQueries::insert,
    updateFn = database.categoryQueries::updateById,
    deleteFn = database.categoryQueries::deleteById,
    fromRow = { row -> Category(row.id, row.name) }
)