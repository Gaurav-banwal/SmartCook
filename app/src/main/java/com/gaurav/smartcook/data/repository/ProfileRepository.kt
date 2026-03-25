package com.gaurav.smartcook.data.repository

import com.gaurav.smartcook.data.remote.firebase.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userId = auth.currentUser?.uid
    private val email = auth.currentUser?.email


    suspend fun getProfile(): UserProfile?{
       return try{
           val doc = firestore.collection("users")
               .document(email.toString())
               .collection("userdata")
               .document("profile")
               .get()
               .await()

           doc.toObject(UserProfile::class.java)
       }catch (e: Exception){
           null
       }
    }

    fun getUserEmail(): String? {
        return email
    }



    suspend fun saveProfile(profile: UserProfile): Boolean {

        return try {
            firestore.collection("users")
                .document(email.toString())
                .collection("userdata")
                .document("profile")
                .set(profile)
                .await()
            true

        } catch (e: Exception) {
            false

        }
    }

    fun logout()
    {
        auth.signOut()
    }


}