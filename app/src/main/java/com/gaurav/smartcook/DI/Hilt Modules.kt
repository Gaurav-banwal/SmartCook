package com.gaurav.smartcook.DI

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.gaurav.smartcook.data.local.AppDatabase
import com.gaurav.smartcook.data.local.IngredientDao
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

}

