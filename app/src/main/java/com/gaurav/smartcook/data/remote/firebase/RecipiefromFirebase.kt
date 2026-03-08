package com.gaurav.smartcook.data.remote.firebase

import com.google.firebase.Timestamp
import java.util.UUID

data class RecipieFromFirebase(
    var ingredients: List<String> = emptyList(),
    var steps: List<String> = emptyList(),
    var name: String = "",
    var servings: Int = 0,
    var summary: String = "",
    var specialNoteUsed: String = "",
    var cooktime: String = "",
    var nutritions: Nutrition = Nutrition(), // Renamed to match Firestore 'nutritions' map
    var visualAnchor: String = "",
    var allergysafe: String ="",
    var id: String = "",
    var imageUrl: String = "",
    var DateModified: Timestamp = Timestamp.now()
)

data class Nutrition(
    var calories: String = "",
    var carbs: String = "",
    var protein: String = "",
    var fat: String = ""
)
