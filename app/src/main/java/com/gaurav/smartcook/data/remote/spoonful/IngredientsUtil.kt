package com.gaurav.smartcook.data.remote.spoonful

import com.gaurav.smartcook.R

object IngredientsUtil {
    private const val CDN_BASE_URL = "https://spoonacular.com/cdn/ingredients_250x250/"
    
    fun getImageUrl(imageFileName: String): String {
        return "$CDN_BASE_URL$imageFileName"
    }

    /**
     * Searches for a recipe by name and returns its first image URL (String).
     * If no recipe is found or an error occurs, returns the default pizza image resource (Int).
     */
    suspend fun getImageForRecipe(recipeName: String): Any {
        return try {
            val response = RetrofitClient.api.searchRecipes(recipeName, 1, MYAPIKEY)
            response.results.firstOrNull()?.image ?: R.drawable.pizza
        } catch (e: Exception) {
            R.drawable.pizza
        }
    }
}
