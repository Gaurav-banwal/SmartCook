package com.gaurav.smartcook.ui.Home


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaurav.smartcook.data.remote.firebase.Nutrition
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.gaurav.smartcook.data.remote.firebase.RecipieFromGemini
import com.gaurav.smartcook.data.remote.spoonful.IngredientsUtil
import com.gaurav.smartcook.data.repository.HomeRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
): ViewModel() {

    var recipie by mutableStateOf<RecipieFromFirebase?>(null)
    var recipeInput by mutableStateOf<userPref?>(null)
    var previousRecipies by mutableStateOf<List<prevRecipie>>(emptyList())
    var idforpass by mutableStateOf("")

    var isLoadingUser by mutableStateOf(false)
        private set

    init {
        fetchUserDetail()
        fetchallpreviousRecipies()
        fetchResToday()
    }

    fun fetchUserDetail() {
        viewModelScope.launch {
            try {
                isLoadingUser = true
                val detail = homeRepository.userDetails()
                detail?.let {
                    recipeInput = it
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching user details", e)
            } finally {
                isLoadingUser = false
            }
        }
    }

    suspend fun generateSmartCookRecipe(): RecipieFromGemini? {
        return homeRepository.generateRecipieRepository(
            recipeInput ?: return null
        )
    }

    fun TransferTofirestore(generatedRecipe: RecipieFromGemini) {
        viewModelScope.launch {
            try {
                val result = IngredientsUtil.getImageForRecipe(generatedRecipe.visualAnchor)
                val url = if (result is String) result else ""

                val recipeSet = RecipieFromFirebase(
                    ingredients = generatedRecipe.ingredients,
                    steps = generatedRecipe.steps,
                    name = generatedRecipe.name,
                    servings = generatedRecipe.servings,
                    summary = generatedRecipe.summary,
                    specialNoteUsed = generatedRecipe.specialNoteUsed,
                    cooktime = generatedRecipe.cooktime,
                    nutritions = Nutrition(
                        calories = generatedRecipe.nutritions.calories,
                        carbs = generatedRecipe.nutritions.carbs,
                        protein = generatedRecipe.nutritions.protein,
                        fat = generatedRecipe.nutritions.fat
                    ),
                    visualAnchor = generatedRecipe.visualAnchor,
                    allergysafe = generatedRecipe.allergysafe,
                    DateModified = Timestamp.now(),
                    id = UUID.randomUUID().toString(),
                    imageUrl = url,
                )

                idforpass = recipeSet.id
                recipie = recipeSet

                homeRepository.saveGlobalRecipie(recipeSet)
                homeRepository.saveToHistory(
                    prevRecipie(
                        id = recipeSet.id,
                        name = recipeSet.name,
                        image = recipeSet.imageUrl,
                        cookTime = recipeSet.cooktime,
                        isFavourite = false
                    )
                )
                fetchallpreviousRecipies()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error transferring to firestore", e)
            }
        }
    }

    fun fetchResToday() {
        viewModelScope.launch {
            try {
                val todayRecipe = homeRepository.getRecipeOfDay()
                recipie = todayRecipe
            } catch (e: Exception) {
                recipie = RecipieFromFirebase(name = "Network Error")
            }
        }
    }

    fun fetchallpreviousRecipies() {
        viewModelScope.launch {
            try {
                previousRecipies = homeRepository.getAllPreviousRecipies()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching history", e)
            }
        }
    }

    fun toogleFavourite(id: String) {
        viewModelScope.launch {
            try {
                val currentItem = previousRecipies.find { it.id == id } ?: return@launch
                val updatedItem = currentItem.copy(isFavourite = !currentItem.isFavourite)

                val success = homeRepository.toggleFavourite(id, updatedItem)
                if (success) {
                    previousRecipies = previousRecipies.map { if (it.id == id) updatedItem else it }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Toggle Favorite failed", e)
            }
        }
    }
}
