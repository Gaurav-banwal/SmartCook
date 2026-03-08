package com.gaurav.smartcook.ui.Inventory

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.gaurav.smartcook.R
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.Ingredient
import com.gaurav.smartcook.data.remote.spoonful.IngredientsUtil
import com.gaurav.smartcook.data.remote.spoonful.MYAPIKEY
import com.gaurav.smartcook.data.remote.spoonful.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


class IngredientViewModel(application: Application): AndroidViewModel(application) {

    var name by mutableStateOf("")
    var quantity by mutableStateOf("")
    private val db = AppDatabase.getDatabase(application)
    val Dao = db.ingredientDao()




    fun addIngredient() {
        viewModelScope.launch {
            Dao.insertIngredient(
                Ingredient(
                    id = name.hashCode(),
                    name = name,
                    quantity = quantity.toInt(),
                    image = ""
                )
            )
            name =""
            quantity = ""

        }

    }
    fun getallitem() = Dao.getAllIngredients()

    fun increaseAmount(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            Dao.increaseAmount(ingredient.name)
        }

    }

        fun decreaseAmount(ingredient: Ingredient) {
            viewModelScope.launch(Dispatchers.IO) {
                if(ingredient.quantity>1)
                Dao.DecreaseAmount(ingredient.name)
                else
                    Dao.deleteIngredient(ingredient)
            }
        }


        fun deleteIngredient(ingredient: Ingredient) {
            viewModelScope.launch {
                Dao.deleteIngredient(
                   ingredient
                )
            }

        }


    private val apiKey = MYAPIKEY


    fun addIngredientWithImage(name: String, qty: Int) {
        viewModelScope.launch {
            try {
                // 1. Fetch from Spoonacular
                val response = RetrofitClient.api.searchIngredients(name, 1, apiKey)
                val firstMatch = response.results.firstOrNull()

                // 2. Build the final URL (Step 5 logic)
                val imageUrl = if (firstMatch != null) {
                    IngredientsUtil.getImageUrl(firstMatch.image)
                } else {
                    "" // Placeholder or default string
                }

                // 3. Save to Room
                Dao.insertIngredient(Ingredient(name = name, quantity = qty, image = imageUrl))
            } catch (e: Exception) {
                // Fallback if network fails
                Dao.insertIngredient(Ingredient(name = name, quantity = qty, image = ""))
            }
        }
    }



    }



