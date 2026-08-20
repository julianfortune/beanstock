package com.julianfortune.beanstock.data.repository

import com.julianfortune.beanstock.data.common.NamedEntity
import kotlinx.coroutines.flow.Flow

// TODO(?): May need to have `ENTITY` and `HEADLINE` type parameters to account for `Item` vs `ItemHeadline`
interface NamedEntityRepository<ENTITY : NamedEntity> {

    fun getAll(): Flow<List<ENTITY>>
    fun getById(id: Long): Flow<ENTITY>
    fun searchByName(query: String): Flow<List<ENTITY>>
    suspend fun insert(name: String): Result<Long>
    suspend fun updateNameById(id: Long, name: String): Result<Long>
    suspend fun deleteById(id: Long): Result<Long>

}
