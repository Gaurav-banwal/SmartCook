package com.gaurav.smartcook.ui.Favourate

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gaurav.smartcook.ui.Home.prevRecipie
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){




    var isLoading by mutableStateOf(false)
    private set
    var favourites by mutableStateOf<List<prevRecipie>>(emptyList())


    fun fetchFavourites() {
        val email = auth.currentUser?.email ?: return
        if(favourites.isEmpty()) isLoading = true

        db.collection("users")
            .document(email)
            .collection("favouriteRecipie")
            .addSnapshotListener { snapshot, e ->
                isLoading = false
                if (e != null) {
                    Log.e("FavouriteViewModel", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    favourites = snapshot.toObjects(prevRecipie::class.java)
                }
            }
    }

    private fun auth() = Firebase.auth

    fun removefromFavourite(id: String) {
        val email = auth.currentUser?.email ?: return
        
        // Optimistic UI update: Remove from local list immediately
        val originalList = favourites
        favourites = favourites.filter { it.id != id }

        // 1. Update previousRecipie status
        db.collection("users")
            .document(email)
            .collection("previousRecipie")
            .document(id)
            .update("isFavourite", false)
            .addOnFailureListener {
                Log.e("FavouriteViewModel", "Failed to update previousRecipie", it)
            }

        // 2. Delete from favouriteRecipie collection
        db.collection("users")
            .document(email)
            .collection("favouriteRecipie")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Log.d("FavouriteViewModel", "Successfully deleted from favourites")
            }
            .addOnFailureListener {
                Log.e("FavouriteViewModel", "Failed to delete from favourites", it)
                // Revert on failure
                favourites = originalList
            }
    }
}
