package com.gaurav.smartcook.DI

import android.content.Context
import androidx.room.Room
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.IngredientDao
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {


    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

      @Provides
      @Singleton
      fun provideAppDataBase(@ApplicationContext context: Context): AppDatabase {
          return Room.databaseBuilder(
             context,
              AppDatabase::class.java,
              "ingredient_database"
          ).build()
      }

    @Provides
    fun provideIngredientDao(database: AppDatabase): IngredientDao {
        return database.ingredientDao()
    }

    private const val CHEF_SYSTEM_INSTRUCTION = """
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
  - are all above points fullfilled"""

    @Singleton
    @Provides
    fun provideGeminiModel(): GenerativeModel {
        // Use the Firebase AI SDK factory which handles the API key via google-services.json 
        // when using GenerativeBackend.googleAI()
        return Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-1.5-flash",
            systemInstruction = content {
                text(CHEF_SYSTEM_INSTRUCTION)
            }
        )
    }

}
