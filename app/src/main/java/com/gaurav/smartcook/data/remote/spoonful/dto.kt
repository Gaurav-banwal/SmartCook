package com.gaurav.smartcook.data.remote.spoonful

data class IngredientResponse(
    val results: List<IngredientDTO>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)

data class IngredientDTO(
    val id: Int,
    val name: String,
    val image: String // This is the filename, e.g., "apple.jpg"
)