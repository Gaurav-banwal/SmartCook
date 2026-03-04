package com.gaurav.smartcook.data.local

import android.media.Image
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val dateModified: Long = System.currentTimeMillis(),
    val image: Int
)
