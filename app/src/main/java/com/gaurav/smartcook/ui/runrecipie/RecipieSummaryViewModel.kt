package com.gaurav.smartcook.ui.runrecipie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipieSummaryViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {



    var recipie by mutableStateOf<RecipieFromFirebase?>(null)
    var isLoading by mutableStateOf(false)
    var isError by mutableStateOf(false)

    fun fetchRecipie(recid: String) {
        if (recid.isEmpty()) return
        
        isLoading = true
        isError = false
        
        val email = auth.currentUser?.email
        
        // We try to fetch from the user's specific path first if they have custom recipes
        if (email != null) {
            db.collection("recipie")
                .document("allrecipies")
                .collection("Recipie")
                .document(recid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        recipie = document.toObject(RecipieFromFirebase::class.java)
                        isLoading = false
                    } else {
                        // If not found in user path, try the global collection
                       // fetchGlobalRecipie(recid)
                    }
                }
                .addOnFailureListener {
                   // fetchGlobalRecipie(recid)
                }
        } else {
           // fetchGlobalRecipie(recid)
        }
    }

    private fun fetchGlobalRecipie(recid: String) {
        db.collection("recipies")
            .document("allrecipies")
            .collection("Recipie")
            .document(recid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    recipie = document.toObject(RecipieFromFirebase::class.java)
                } else {
                    isError = true
                }
                isLoading = false
            }
            .addOnFailureListener {
                isError = true
                isLoading = false
            }
    }
}
