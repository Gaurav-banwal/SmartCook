package com.gaurav.smartcook.data.remote.firebase

import com.google.firebase.Timestamp
import java.util.UUID

data class RecipieFromFirebase(
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val name: String = "",
    val servings: Int = 0,
    val summary: String = "",
    val specialNoteUsed: String = "",
    val cooktime: String = "",
    val nutrition: Nutrition = Nutrition(),
    val visualAnchor: String = "",
    val allergysafe: String ="",
    val id: String ,
    val imageUrl: String = "",
    val DateModified: Timestamp = Timestamp.now()
)

data class Nutrition(
    val calories: String = "",
    val carbs: String = "",
    val protein: String = "",
    val fat: String = ""
)
//
//data class RecipieFromGemini(
//    val ingredients: List<String> = emptyList(),
//    val steps: List<String> = emptyList(),
//    val name: String = "",
//    val servings: Int = 0,
//    val summary: String = "",
//    val specialNoteUsed: String = "",
//    val cooktime: String = "",
//    val nutrition: Nutrition = Nutrition(),
//    val visualAnchor: String = "",
//    val allergysafe: String =""
//)

