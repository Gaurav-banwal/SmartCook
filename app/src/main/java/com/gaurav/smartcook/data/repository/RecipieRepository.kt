package com.gaurav.smartcook.data.repository

import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipieRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userId = auth.currentUser?.uid

    private val email = auth.currentUser?.email

    suspend fun getRecipieBYId(recipieid: String): RecipieFromFirebase?{
        return try{
            val doc = firestore.collection("recipie")
                .document("allrecipies")
                .collection("Recipie")
                .document(recipieid)
                .get()
                .await()
            if(doc.exists()){
                doc.toObject(RecipieFromFirebase::class.java)
            }else{
                null
            }
        }catch (e: Exception){
            null
        }

    }

    suspend fun saveRecipie(recipie: RecipieFromFirebase) {

        firestore.collection("recipie")
            .document("allrecipies")
            .collection("Recipie")
            .document(recipie.id)
            .set(recipie)
            .await()

    }





}