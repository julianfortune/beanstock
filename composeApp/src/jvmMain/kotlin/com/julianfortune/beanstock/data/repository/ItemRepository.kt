package com.julianfortune.beanstock.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.julianfortune.beanstock.core.util.unwrapUnsafe
import com.julianfortune.beanstock.data.codec.WeightListCodec
import com.julianfortune.beanstock.data.model.Category
import com.julianfortune.beanstock.data.model.Item
import com.julianfortune.beanstock.data.model.ItemHeadline
import com.julianfortune.beanstock.data.model.Weight
import com.julianfortune.beanstock.db.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepository(private val database: Database) {

    fun getAll(): Flow<List<ItemHeadline>> {
        return database.itemQueries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { items ->
                items.map {
                    ItemHeadline(it.id, it.name)
                }
            }
    }

    fun getById(id: Long): Flow<Item> {
        return database.itemQueries.getItemWithCategoriesById(id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val first = rows.firstOrNull() ?: throw RuntimeException("Unable to find an Item with id=$id")
                val categories = rows
                    .filter { it.categoryId != null } // Since it's a LEFT JOIN there may be no category
                    .map { Category(it.categoryId!!, it.categoryName!!) }
                val savedWeights = first.savedWeightInCentigramsListJson?.let {
                    WeightListCodec.deserialize(it).unwrapUnsafe().toSet()
                }
                val format = formatFromSavedWeights(savedWeights)

                Item(
                    first.id,
                    first.name,
                    categories,
                    format
                )
            }

    }

    fun searchByName(searchString: String): Flow<List<Item>> {
        return database.itemQueries.getAllItemsWithCategoriesByQueryingName(
            query = "%$searchString%",
            startsWith = "$searchString%",
            exactMatch = searchString,
            limit = 50,
        )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.id }
                    .map { (itemId, itemRows) ->
                        val firstRow = itemRows.first()
                        val categories = rows
                            .filter { it.categoryId != null } // Since it's a LEFT JOIN there may be no category
                            .map { Category(it.categoryId!!, it.categoryName!!) }
                        val savedWeights = firstRow.savedWeightInCentigramsListJson?.let {
                            WeightListCodec.deserialize(it).unwrapUnsafe().toSet()
                        }
                        val format = formatFromSavedWeights(savedWeights)

                        Item(
                            itemId,
                            firstRow.name,
                            categories,
                            format,
                        )
                    }
            }
    }

    private fun formatFromSavedWeights(savedWeights: Set<Weight>?): Item.Format {
        return when (savedWeights?.size) {
            null, 0 -> Item.Format.Loose
            else -> Item.Format.Packaged(savedWeights)
        }
    }

    suspend fun insert(name: String, categoryIds: Set<Long>, format: Item.Format): Result<Long> {
        return Result.runCatching {
            database.transactionWithResult {
                val savedWeightsJson = serializeFormatToJsonList(format)

                val itemId = database.itemQueries.insert(
                    name,
                    savedWeightsJson
                ).awaitAsOne()

                categoryIds.forEach { categoryId ->
                    database.itemCategoryQueries.insert(itemId, categoryId)
                }

                itemId
            }
        }
    }

    suspend fun update(
        itemId: Long,
        name: String,
        categoryIds: Set<Long>,
        format: Item.Format,
    ): Result<Long> {
        val savedWeightsJson = serializeFormatToJsonList(format)

        return runCatching {
            database.transactionWithResult {
                database.itemQueries.updateById(name, savedWeightsJson, itemId)

                database.itemCategoryQueries.deleteByItemId(itemId)
                categoryIds.forEach { categoryId ->
                    database.itemCategoryQueries.insert(itemId, categoryId)
                }

                itemId
            }
        }
    }

    private fun serializeFormatToJsonList(format: Item.Format): String? {
        return when (format) {
            is Item.Format.Loose -> null
            is Item.Format.Packaged -> WeightListCodec.serialize(format.sizes.toList())
        }
    }

    suspend fun deleteById(id: Long): Result<Long> {
        return Result.runCatching {
            database.itemQueries.deleteById(id) // `ItemCategories` are deleted automatically by CASCADE-ing
        }.fold(
            onSuccess = { rowsDeleted ->
                if (rowsDeleted > 0) Result.success(id)
                else Result.failure(IllegalStateException("Item with id=$id could not be deleted"))
            },
            onFailure = { Result.failure(it) }
        )
    }
}
