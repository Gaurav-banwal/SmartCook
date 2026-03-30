package com.gaurav.smartcook.data.repository


import com.gaurav.smartcook.data.local.IngredientDao
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.gaurav.smartcook.data.remote.firebase.RecipieFromGemini
import com.gaurav.smartcook.ui.Home.prevRecipie
import com.gaurav.smartcook.ui.Home.userPref
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository  @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private  val dao: IngredientDao,
    private val model: GenerativeModel
) {

    private fun historyCollection() = email?.let {
        db.collection("users").document(it).collection("previousRecipie")
    }

    private val email: String? get() = auth.currentUser?.email

    suspend fun userDetails():userPref?{
        val useremail = email?: return null

        return try{
            val doc = db.collection("users")
                .document(useremail)
                .collection("userdata")
                .document("profile")
                .get()
                 .await()

           if(doc.exists()){
               doc.toObject(userPref::class.java)
            }else null
        }
        catch (e: Exception){
            null
        }
    }

    suspend fun  getAllIngredients(): String {

        return try {
            val list = dao.getAllIngredientsName().first()
           if(list.isEmpty()) return "No ingredient in inventory"
            else  list.joinToString(separator = ", ") { ingredient ->
               "${ingredient.name}: ${ingredient.quantity} ${ingredient.unit}"
           }

        }catch (e: Exception){
            return ""

        }

    }

    suspend fun generateRecipieRepository(input: userPref): RecipieFromGemini?{


        val pantryItems = getAllIngredients()

        val userPrompt = """
        Pantry Ingredients: $pantryItems
        Preferred Ingredients: ${input.ingredients}
        Allergy Information: ${input.allergyInfo}
        Diet Type Selection: ${input.diet}
        Serve Size:  ${input.ServeSize}
        Additional Note: ${input.neededNote}
        Special Note: ${input.specialNote}
        [CURRENT_TIME]: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}
    """.trimIndent()

        return try {
            val response = model.generateContent(userPrompt)
            val jsonString = response.text ?: return null

            Json { ignoreUnknownKeys = true }.decodeFromString<RecipieFromGemini>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }


    }

   suspend fun saveGlobalRecipie(recipeSet: RecipieFromFirebase) {
        try {
            db.collection("recipie")
                .document("allrecipies")
                .collection("Recipie")
                .document(recipeSet.id)
                .set(recipeSet)
                .await()
        }catch (e: Exception){
            //TODO
        }


    }
     suspend fun saveToHistory(prevRecipie: prevRecipie) {
         val  collection = historyCollection()?:return
         try {
             val snapshot = collection.get().await()
             if (snapshot.size()>=10)
                 snapshot.documents.firstOrNull()?.reference?.delete()

         }catch (e: Exception) {
             //TODO
         }


    }
     suspend fun getRecipeOfDay(): RecipieFromFirebase? {
         return try {
             val query = db.collection("recipie")
                 .document("allrecipies")
                 .collection("RecipieOfDay")
                 .orderBy("DateModified", Query.Direction.DESCENDING)
                 .limit(1)
                 .get()
                 .await()
             query.documents.firstOrNull()?.toObject(RecipieFromFirebase::class.java)

         }catch (e: Exception){
          null
         }
    }

     suspend fun toggleFavourite(id: String, updatedItem: prevRecipie): Boolean {
        val collection = historyCollection() ?: return false
            return try {
                collection.document(id).set(updatedItem).await()
                true
            } catch (e: Exception) {
                false
            }
    }

     suspend fun getAllPreviousRecipies(): List<prevRecipie> {
        val collection = historyCollection() ?: return emptyList()
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(prevRecipie::class.java) // Note: might need manual mapping if IDs vary
            snapshot.documents.mapNotNull { it.toObject(prevRecipie::class.java) }
        } catch (e: Exception) {
            emptyList()
        }

    }


}




