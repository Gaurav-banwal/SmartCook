package com.gaurav.smartcook.data.repository

import com.gaurav.smartcook.data.local.Ingredient
import com.gaurav.smartcook.data.local.IngredientDao
import com.gaurav.smartcook.data.local.ingredientData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientRepository @Inject constructor(
    private val dao: IngredientDao
) {
    // Get all ingredients as a Flow (Real-time updates)
    fun getAllIngredients(): Flow<List<Ingredient>> = dao.getAllIngredients()

    // Database operations
    suspend fun insertIngredient(ingredient: Ingredient) = dao.insertIngredient(ingredient)

    suspend fun deleteIngredient(ingredient: Ingredient) = dao.deleteIngredient(ingredient)

    suspend fun searchIngredients(name: String): List<Ingredient> = dao.searchIngredients(name)

    suspend fun increaseAmount(name: String) = dao.increaseAmount(name)

     fun getAllIngredientsName(): Flow<List<ingredientData>> = dao.getAllIngredientsName()

    suspend fun decreaseAmount(name: String) = dao.DecreaseAmount(name)


}