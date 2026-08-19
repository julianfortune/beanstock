package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.Query
import com.julianfortune.beanstock.data.model.Program
import com.julianfortune.beanstock.db.Database
import com.julianfortune.beanstock.db.Program as DbProgram

class ProgramRepository(database: Database) : NamedEntityRepository<DbProgram, Program> {
    override val entityName = "Program"

    override fun fromRow(row: DbProgram): Program {
        return Program(row.id, row.name)
    }

    override val getAllFn: () -> Query<DbProgram> = database.programQueries::getAll
    override val insertFn = database.programQueries::insert
    override val updateFn = database.programQueries::updateById
    override val deleteFn = database.programQueries::deleteById
}
