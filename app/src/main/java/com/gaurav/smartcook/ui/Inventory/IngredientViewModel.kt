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
import kotlinx.coroutines.launch


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
                    image = R.drawable.pizza
                )
            )
            name =""
            quantity = ""

        }

    }
    fun getallitem() = Dao.getAllIngredients()

    fun increaseAmount(ingredient: Ingredient) {
        viewModelScope.launch {
            Dao.increaseAmount(ingredient.name)
        }

    }

        fun decreaseAmount(ingredient: Ingredient) {
            viewModelScope.launch {
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
    }



