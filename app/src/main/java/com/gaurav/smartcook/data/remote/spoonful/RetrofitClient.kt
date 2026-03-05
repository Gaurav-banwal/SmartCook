package com.gaurav.smartcook.data.remote.spoonful

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.spoonacular.com/"

    val api: SpoonacularApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Moshi converts the JSON string into your Kotlin Data Classes automatically
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpoonacularApi::class.java)
    }
}