package com.gaurav.smartcook.ui.Home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.remote.firebase.Nutrition
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.gaurav.smartcook.data.remote.firebase.RecipieFromGemini
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.serialization.json.Json
import java.util.UUID

data class userPref(
    var ingredients: String = "",
    var allergyInfo: String = "",
    var diet: String = "",
    var neededNote: String = "",
    var specialNote: String = ""
)

class HomeViewModel(application: Application): AndroidViewModel(application){

    private val db = Firebase.firestore
    private val dbroom = AppDatabase.getDatabase(application)
    private val dao = dbroom.ingredientDao()
    private val auth = Firebase.auth
    
    var recipie by mutableStateOf<RecipieFromFirebase?>(null)
    var recipeInput by mutableStateOf<userPref?>(null)

    var idforpass by mutableStateOf("")


    val config = GenerationConfig.builder()
        .setResponseMimeType("application/json")
        .build()

    val chefSystemInstruction = """
    ROLE: Senior Culinary Architect.
    GOAL: Create a professional recipe by SELECTING a logical subset (3-5 primary items) from provided ingredients.
    CONSTRAINTS: 
    - IGNORE unrelated ingredients. 
    - NEUTRAL ALLERGY PROTOCOL: Never use items from [USER_ALLERGIES].
    - Assume pantry staples (Water, Salt, Sugar, Oil, Indian spices).
    - visualAnchor must be a generic common dish name for image matching.
    - MEAL TIMING: Adjust the portion size and "heaviness" based on the [CURRENT_TIME].
    - OUTPUT: Return ONLY raw JSON matching this EXACT schema.
    
    CRITICAL: The "ingredients" field MUST be a simple list of strings (e.g., ["1 cup Chicken", "2 tsp Salt"]). 
    DO NOT use objects for ingredients.
    
    Schema:
    {
      "ingredients": ["string", "string"],
      "steps": ["string", "string"],
      "name": "string",
      "servings": 0,
      "summary": "string",
      "specialNoteUsed": "string",
      "cooktime": "string",
      "nutritions": {
        "calories": "string",
        "carbs": "string",
        "protein": "string",
        "fat": "string"
      },
      "visualAnchor": "string",
      "allergysafe": "string"
    }
""".trimIndent()

    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash-lite", generationConfig = config,
            systemInstruction = content {
                text(chefSystemInstruction)
            })

    fun fetchUserDetail(){
        val email = auth.currentUser?.email
        if (email == null) return

        db.collection("users")
             .document(email)
             .collection("userdata")
             .document("profile")
             .get()
             .addOnSuccessListener { document ->
                 if (document.exists()) {
                     recipeInput = document.toObject(userPref::class.java)
                 } else {
                     recipeInput = userPref()
                 }
             }
             .addOnFailureListener { 
                 recipeInput = userPref(ingredients = "Error loading preferences")
             }
    }

    suspend fun generateSmartCookRecipe(): RecipieFromGemini? {

        val input = recipeInput ?: return null

        val pantryItems = getAllIngredients()

        val userPrompt = """
        Pantry Ingredients: $pantryItems
        Preferred Ingredients: ${input.ingredients}
        Allergy Information: ${input.allergyInfo}
        Diet Type Selection: ${input.diet}
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
    
    // Helper to use Date()
    private fun Date() = Calendar.getInstance().time

    //Tranferring recipie to Firestore
    ///recipie/allrecipies/Recipie/AX3B
    fun TransferTofirestore(generatedRecipe: RecipieFromGemini){
        val email = auth.currentUser?.email ?: return


        val recipeSet= RecipieFromFirebase(
            ingredients = generatedRecipe.ingredients,
            steps = generatedRecipe.steps,
            name = generatedRecipe.name,
            servings = generatedRecipe.servings,
            summary = generatedRecipe.summary,
            specialNoteUsed = generatedRecipe.specialNoteUsed,
            cooktime = generatedRecipe.cooktime,
            nutritions = Nutrition(
                calories = generatedRecipe.nutritions.calories,
                carbs = generatedRecipe.nutritions.carbs,
                protein = generatedRecipe.nutritions.protein,
                fat = generatedRecipe.nutritions.fat
            ),
            visualAnchor = generatedRecipe.visualAnchor,
            allergysafe = generatedRecipe.allergysafe,
            DateModified = Timestamp.now(),
            id = UUID.randomUUID().toString(),
            imageUrl = "",
        )

        idforpass = recipeSet.id

         db.collection("recipie")
             .document("allrecipies")
             .collection("Recipie")
             .document(recipeSet.id)
             .set(recipeSet)
             .addOnSuccessListener {
                 Log.d("Success","Recipe Added")
             }
             .addOnFailureListener {
                  Log.d("Failure","Recipe Not Added")
             }

    }

    fun fetchResToday(){
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formatted = formatter.format(time)

        db.collection("recipie")
            .document("allrecipies")
            .collection("RecipieOfDay")
            .document(formatted)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists())
                    recipie = document.toObject(RecipieFromFirebase::class.java)
                else
                    recipie = RecipieFromFirebase(name = "No Recipe Today")
            }
            .addOnFailureListener {
                recipie = RecipieFromFirebase(name = "Network Error")
            }
    }

    suspend fun getAllIngredients(): String {
        return try {
            val list = dao.getAllIngredientsName().first()
            list.joinToString(separator = ", ") { ingredient ->
                "${ingredient.name}: ${ingredient.quantity} ${ingredient.unit}"
            }
        } catch (e: Exception) {
            ""
        }
    }
}
