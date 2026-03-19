package com.gaurav.smartcook.ui.Home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.IngredientDao
import com.gaurav.smartcook.data.remote.firebase.Nutrition
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.gaurav.smartcook.data.remote.firebase.RecipieFromGemini
import com.gaurav.smartcook.data.remote.spoonful.IngredientsUtil
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db : FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dao: IngredientDao
): ViewModel() {

  //  private val db = Firebase.firestore
  //  private val dbroom = AppDatabase.getDatabase(Application())
   // private val dao = dbroom.ingredientDao()
   // private val auth = Firebase.auth

    var recipie by mutableStateOf<RecipieFromFirebase?>(null)
    var recipeInput by mutableStateOf<userPref?>(null)
    var previousRecipies by mutableStateOf<List<prevRecipie>>(emptyList())

    var idforpass by mutableStateOf("")


    val config = GenerationConfig.builder()
        .setResponseMimeType("application/json")
        .build()

    val chefSystemInstruction = """
  ROLE: Senior Culinary Architect.
  GOAL: Create a professional, balanced recipe for EXACTLY [SERVE_SIZE] people.

  ### CRITICAL LOGIC & RATIOS:
  1. **SCALING OVER UTILIZATION:** You are a professional chef, not a cleaner. Do NOT use the entire quantity of an ingredient just because it is provided. 
     - *Example:* If the user has 10kg of flour and wants 2 servings of pancakes, use "1 cup," NOT "10kg."
     - Prioritize culinary balance. Only use what is mathematically necessary for [SERVE_SIZE].
  2. **STEP DEPTH:** Steps must be "well-explained." Use descriptive, professional sentences (e.g., "Gently fold the dry ingredients into the wet mixture until just combined to avoid overworking the gluten"). 
     - Minimum 4-6 detailed steps.
  3. **INGREDIENT SELECTION:** Select 3-5 primary items. Ignore unrelated inputs. Use pantry staples (Water, Salt, Sugar, Oil, Indian spices) as needed.
  4. **MEAL TIMING:** Adjust "heaviness" based on [CURRENT_TIME].
  5. **NUTRITION:** Return ONLY `value + unit`. Strictly NO "approx" or "about.",(dont use term calories use only kcal)
  6. **ALLERGY PROTOCOL:** Zero-tolerance for [USER_ALLERGIES].
  7. Visual Anchor should be the name of the closest resembling dish from generated dish , and visual anchor should only be of 2 -3 words only
  8. Always see the type of diet before producing result and check if recipie matches the kind of diet
  ### OUTPUT SCHEMA (STRICT JSON):
  {
    "name": "string",
    "summary": "string",
    "ingredients": ["string", "string"],
    "steps": [
      "Step 1: [Detailed prep with technique...]",
      "Step 2: [Detailed cooking with heat/timing...]",
      "Step 3: [Detailed finishing/plating...]"
    ],
    "servings": [SERVE_SIZE],
    "cooktime": "string",
    "nutritions": {
      "calories": "string",
      "carbs": "string",
      "protein": "string",
      "fat": "string"
    },
    "specialNoteUsed": "string",
    "visualAnchor": "string",
    "allergysafe": "string"
  }

  ### FINAL SANITY CHECK:
  - Did I use a reasonable amount of ingredients for [SERVE_SIZE]? 
  - Is the `servings` value exactly [SERVE_SIZE]?
  - Is the dish resembles type of user diet(eg. if user ask for veg , it shouldn't contain non vegetarian item even if present in the inventory) 
  - Are the steps detailed enough for a beginner to follow professionally?
  - are all above points fullfilled
""".trimIndent()

    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            "gemini-2.5-flash-lite", generationConfig = config,
            systemInstruction = content {
                text(chefSystemInstruction)
            })

    fun fetchUserDetail() {
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

    // Helper to use Date()
    private fun Date() = Calendar.getInstance().time

    //Tranferring recipie to Firestore
    ///recipie/allrecipies/Recipie/AX3B
    suspend fun TransferTofirestore(generatedRecipe: RecipieFromGemini) {
        val email = auth.currentUser?.email ?: return


        val result = IngredientsUtil.getImageForRecipe(generatedRecipe.visualAnchor)
        val url = if (result is String) result else ""

        val recipeSet = RecipieFromFirebase(
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
            imageUrl = url,
        )

        idforpass = recipeSet.id
        recipie = recipeSet

        db.collection("recipie")
            .document("allrecipies")
            .collection("Recipie")
            .document(recipeSet.id)
            .set(recipeSet)
            .addOnSuccessListener {
                Log.d("Success", "Recipe Added")
                // saveToHistory()
                //transferToFirebase()
              //  checkAndLimitHistory()
                checkAndSaveToHistory()
            }
            .addOnFailureListener {
                Log.d("Failure", "Recipe Not Added")
            }

    }

    fun fetchResToday() {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formatted = formatter.format(time)

        db.collection("recipie")
            .document("allrecipies")
            .collection("RecipieOfDay")
            .document(formatted)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists())
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






    fun fetchallpreviousRecipies() {
        val email = auth.currentUser?.email ?: return
        db.collection("users")
            .document(email)
            .collection("previousRecipie")
            .get()
            .addOnSuccessListener { documents ->
                previousRecipies = documents.toObjects(prevRecipie::class.java)
            }
            .addOnFailureListener {
                Log.e("HomeViewModel", "Error fetching previous recipes", it)
            }
    }

    fun toogleFavourite(id: String) {
        val recipie = previousRecipies.find { it.id == id } ?: return
        val updatedRecipie = recipie.copy(isFavourite = !recipie.isFavourite)
        if (updatedRecipie.isFavourite == true) {
            db.collection("users")
                .document(auth.currentUser?.email ?: return)
                .collection("favouriteRecipie")
                .document(id)
                .set(updatedRecipie)
                .addOnSuccessListener {
                    Log.d("Success", "Recipe Favorite Toggled")
                    // Update local state after successful remote update
                    previousRecipies =
                        previousRecipies.map { if (it.id == id) updatedRecipie else it }


                    updateprev(id, updatedRecipie)
                }
                .addOnFailureListener {
                    Log.d("Failure", "Failed to toggle Favorite")
                }


        } else {
            db.collection("users")
                .document(auth.currentUser?.email ?: return)
                .collection("favouriteRecipie")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    Log.d("Success", "Recipe Favorite Toggled")
                    updateprev(id, updatedRecipie)
                }
                .addOnFailureListener {
                    Log.d("Failure", "Failed to toggle Favorite")
                }
        }


    }

    private fun updateprev(id: String, updatedRecipie: prevRecipie) {
        val email = auth.currentUser?.email ?: return

        // Update the main history collection so it stays favorited on next app launch
        db.collection("users")
            .document(email)
            .collection("previousRecipie")
            .document(id)
            .update("isFavourite", updatedRecipie.isFavourite)

        // Update the local list so the UI heart icon changes immediately
        previousRecipies = previousRecipies.map {
            if (it.id == id) updatedRecipie else it
        }
        Log.d("Success", "Favorite state synced locally and remotely")
    }

    fun checkAndSaveToHistory() {
        val email = auth.currentUser?.email ?: return
        val currentRecipe = recipie ?: return
        val historyRef = db.collection("users").document(email).collection("previousRecipie")

        historyRef.get().addOnSuccessListener { snapshots ->
            val recipeId = currentRecipe.id.ifEmpty { idforpass }
            val historyItem = prevRecipie(
                id = recipeId,
                name = currentRecipe.name,
                image = currentRecipe.imageUrl,
                cookTime = currentRecipe.cooktime,
                isFavourite = false
            )

            // If at or over limit, delete the oldest FIRST
            if (snapshots.size() >= 10) {
                val oldestDoc = snapshots.documents.firstOrNull()
                oldestDoc?.reference?.delete()?.addOnSuccessListener {
                    // AFTER delete, add the new one
                    historyRef.document(recipeId).set(historyItem).addOnSuccessListener {
                        fetchallpreviousRecipies()
                    }
                }
            } else {
                // NOT at limit, just add the new one immediately
                historyRef.document(recipeId).set(historyItem).addOnSuccessListener {
                    fetchallpreviousRecipies()
                }
            }
        }
    }


}
