package com.gaurav.smartcook.data.remote.spoonful

object IngredientUtils {
    private const val CDN_BASE_URL = "https://spoonacular.com/cdn/ingredients_250x250/"

    fun getImageUrl(imageFileName: String): String {
        // Example: "apple.jpg" becomes "https://spoonacular.com/cdn/ingredients_250x250/apple.jpg"
        return "$CDN_BASE_URL$imageFileName"
    }
}