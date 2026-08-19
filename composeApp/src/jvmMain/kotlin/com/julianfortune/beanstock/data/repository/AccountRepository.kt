package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.Query
import com.julianfortune.beanstock.data.model.Account
import com.julianfortune.beanstock.db.Database
import com.julianfortune.beanstock.db.PurchasingAccount as DbPurchasingAccount

class AccountRepository(database: Database) : NamedEntityRepository<DbPurchasingAccount, Account> {
    override val entityName = "PurchasingAccount"

    override fun fromRow(row: DbPurchasingAccount): Account {
        return Account(row.id, row.name)
    }

    override val getAllFn: () -> Query<DbPurchasingAccount> = database.purchasingAccountQueries::getAll
    override val insertFn = database.purchasingAccountQueries::insert
    override val updateFn = database.purchasingAccountQueries::updateById
    override val deleteFn = database.purchasingAccountQueries::deleteById
}
