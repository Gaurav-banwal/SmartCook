package com.gaurav.smartcook.ui.Setting



import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.gaurav.smartcook.data.remote.firebase.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SettingsViewModel(application: Application)  : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth // Initialize Auth

    var userProfile = mutableStateOf<UserProfile?>(null)
    var isLoading = mutableStateOf(false)
    var isUpdating = mutableStateOf(false)


    //ui

    var name by mutableStateOf("")
    var age by mutableStateOf("")
    var gender by mutableStateOf("")
    var diet by mutableStateOf("")
    var servesize by mutableFloatStateOf(1f)
    var allergy by mutableStateOf("")
    var specialNote by mutableStateOf("")


    fun loadProfileIntoState(profile: UserProfile) {
        name = profile.Name
        age = profile.Age.toString()
        gender = profile.Gender
        diet = profile.Diet
        servesize = profile.Servesize.toFloat()
        allergy = profile.Allergy
        specialNote = profile.Specialcooknote
    }


    init {
        fetchUserDataFromAuth()
    }

    fun fetchUserDataFromAuth() {
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email != null) {
            isLoading.value = true


            db.collection("users")
                .document(email)
                .collection("userdata")
                .document("profile")
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userProfile.value = document.toObject(UserProfile::class.java)
                    }
                    isLoading.value = false
                }
                .addOnFailureListener {
                    isLoading.value = false
                }
        } else {
            println("No user is signed in via Firebase Auth")
        }
    }

    fun updateUserData(updatedProfile: UserProfile, onComplete: (Boolean) -> Unit) {
        val email = auth.currentUser?.email

         val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null && email != null) {
            isUpdating.value = true


            db.collection("users")
                .document(email)
                .collection("userdata")
                .document("profile")
                .set(updatedProfile)
                .addOnSuccessListener {
                    userProfile.value = updatedProfile
                    isUpdating.value = false
                    onComplete(true)
                }
                .addOnFailureListener {
                    isUpdating.value = false
                    onComplete(false)
                }
        }
    }
}