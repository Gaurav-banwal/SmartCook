package com.gaurav.smartcook.ui.Inventory


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.gaurav.smartcook.data.local.Ingredient
import com.gaurav.smartcook.data.local.IngredientDao
import com.gaurav.smartcook.data.remote.spoonful.IngredientsUtil
import com.gaurav.smartcook.data.remote.spoonful.MYAPIKEY
import com.gaurav.smartcook.data.remote.spoonful.RetrofitClient
import com.gaurav.smartcook.data.repository.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
//

@HiltViewModel
class IngredientViewModel @Inject constructor(
    private val repository: IngredientRepository
): ViewModel(){

    var name by mutableStateOf("")
    var quantity by mutableStateOf("")
    var unit by mutableStateOf("")
    


    // Expose ingredients as a StateFlow for better state management
    val ingredients: StateFlow<List<Ingredient>> = repository.getAllIngredients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

//    fun addIngredient() {
//        viewModelScope.launch {
//            repository.insertIngredient(
//                Ingredient(
//                    id = name.hashCode(),
//                    name = name,
//                    quantity = quantity.toIntOrNull() ?: 0,
//                    image = "",
//                    unit = unit
//                )
//            )
//            name = ""
//            quantity = ""
//            unit = ""
//        }
//    }
//
//    // Kept for backward compatibility if needed, but prefer 'ingredients' property
//    fun getallitem() = repository.getAllIngredients()

    fun increaseAmount(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            if (ingredient.quantity < 999999)
                repository.increaseAmount(ingredient.name)
        }
    }

    fun decreaseAmount(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            if (ingredient.quantity > 1)
                repository.decreaseAmount(ingredient.name)
            else
                repository.deleteIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIngredient(ingredient)
        }
    }

    private val apiKey = MYAPIKEY

    fun addIngredientWithImage(name: String, qty: Int, unit: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.searchIngredients(name, 1, apiKey)
                val firstMatch = response.results.firstOrNull()
                val imageUrl = if (firstMatch != null) {
                    IngredientsUtil.getImageUrl(firstMatch.image)
                } else {
                    "" 
                }

                repository.insertIngredient(Ingredient(
                    name = name, 
                    quantity = qty, 
                    image = imageUrl,
                    unit = unit
                ))
            } catch (e: Exception) {
                repository.insertIngredient(Ingredient(
                    name = name, 
                    quantity = qty, 
                    image = "",
                    unit = unit
                ))
            }
        }
    }
}
