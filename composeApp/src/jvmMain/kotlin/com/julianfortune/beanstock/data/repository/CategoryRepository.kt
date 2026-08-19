package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.Query
import com.julianfortune.beanstock.data.model.Category
import com.julianfortune.beanstock.db.Database
import com.julianfortune.beanstock.db.Category as DbCategory

class CategoryRepository(database: Database) : NamedEntityRepository<DbCategory, Category> {
    override val entityName = "Category"

    override fun fromRow(row: DbCategory): Category {
        return Category(row.id, row.name)
    }

    override val getAllFn: () -> Query<DbCategory> = database.categoryQueries::getAll
    override val insertFn = database.categoryQueries::insert
    override val updateFn = database.categoryQueries::updateById
    override val deleteFn = database.categoryQueries::deleteById
}
