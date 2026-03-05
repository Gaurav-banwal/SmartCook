package com.gaurav.smartcook.data.remote.spoonful

import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("food/ingredients/search")
    suspend fun searchIngredients(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String
    ): IngredientResponse
}