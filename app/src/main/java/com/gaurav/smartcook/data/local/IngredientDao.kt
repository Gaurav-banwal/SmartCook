package com.gaurav.smartcook.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Query("SELECT * FROM Ingredient ORDER BY DateModified DESC")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Query("SELECT * FROM Ingredient WHERE name LIKE '%' || :name || '%' ORDER BY DateModified DESC")
  suspend  fun searchIngredients(name: String): List<Ingredient>


    @Query("Update Ingredient SET quantity = quantity + 1 WHERE name = :name")
   suspend fun increaseAmount(name: String)

    @Query("Update Ingredient SET quantity = quantity - 1 WHERE name = :name")
    suspend fun DecreaseAmount(name: String)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)
}
