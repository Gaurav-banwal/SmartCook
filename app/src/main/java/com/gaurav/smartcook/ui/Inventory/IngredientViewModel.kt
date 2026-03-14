package com.gaurav.smartcook.ui.Inventory

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.Ingredient
import com.gaurav.smartcook.data.remote.spoonful.IngredientsUtil
import com.gaurav.smartcook.data.remote.spoonful.MYAPIKEY
import com.gaurav.smartcook.data.remote.spoonful.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class IngredientViewModel(application: Application): AndroidViewModel(application) {

    var name by mutableStateOf("")
    var quantity by mutableStateOf("")
    var unit by mutableStateOf("")
    
    private val db = AppDatabase.getDatabase(application)
    val Dao = db.ingredientDao()

    // Expose ingredients as a StateFlow for better state management
    val ingredients: StateFlow<List<Ingredient>> = Dao.getAllIngredients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addIngredient() {
        viewModelScope.launch {
            Dao.insertIngredient(
                Ingredient(
                    id = name.hashCode(),
                    name = name,
                    quantity = quantity.toIntOrNull() ?: 0,
                    image = "",
                    unit = unit
                )
            )
            name = ""
            quantity = ""
            unit = ""
        }
    }
    
    // Kept for backward compatibility if needed, but prefer 'ingredients' property
    fun getallitem() = Dao.getAllIngredients()

    fun increaseAmount(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            if (ingredient.quantity < 999999)
                Dao.increaseAmount(ingredient.name)
        }
    }

    fun decreaseAmount(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            if (ingredient.quantity > 1)
                Dao.DecreaseAmount(ingredient.name)
            else
                Dao.deleteIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            Dao.deleteIngredient(ingredient)
        }
    }

    private val apiKey = MYAPIKEY

    fun addIngredientWithImage(name: String, qty: Int, unit: String = "") {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.searchIngredients(name, 1, apiKey)
                val firstMatch = response.results.firstOrNull()
                val imageUrl = if (firstMatch != null) {
                    IngredientsUtil.getImageUrl(firstMatch.image)
                } else {
                    "" 
                }

                Dao.insertIngredient(Ingredient(
                    name = name, 
                    quantity = qty, 
                    image = imageUrl,
                    unit = unit
                ))
            } catch (e: Exception) {
                Dao.insertIngredient(Ingredient(
                    name = name, 
                    quantity = qty, 
                    image = "",
                    unit = unit
                ))
            }
        }
    }
}
