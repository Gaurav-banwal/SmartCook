package com.gaurav.smartcook.ui.runrecipie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gaurav.smartcook.data.remote.firebase.Nutrition
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RecipieSummaryViewModel: ViewModel() {

    // FIX: Change to mutableStateOf so Compose can observe changes
    var recipie by mutableStateOf<RecipieFromFirebase?>(
        RecipieFromFirebase(
            name = "Loading...",
            summary = "",
            cooktime = "",
            servings = 0,
            ingredients = emptyList(),
            steps = emptyList(),
            nutrition = Nutrition(
                calories = "",
                carbs = "",
                protein = "",
                fat = ""
            ),
            allergysafe = "",
            id = ""
        )
    )

    private val db = Firebase.firestore
    var recid by mutableStateOf("")

    fun fetchRecipie(){
        if (recid.isEmpty()) return
        
        db.collection("recipies")
            .document("allrecipies")
            .collection("Recipie")
            .document(recid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    recipie = document.toObject(RecipieFromFirebase::class.java)
                }
            }
            .addOnFailureListener {
                 recipie = RecipieFromFirebase(
                     name = "Error",
                      id = "",
                     summary = "",
                     cooktime = "",
                     servings = 0,
                     ingredients = emptyList(),
                     steps = emptyList(),
                     nutrition = Nutrition(
                         calories = "",
                         carbs = "",
                         protein = "",
                         fat = ""
                     ),
                     allergysafe = ""
                 )
            }
    }
}
