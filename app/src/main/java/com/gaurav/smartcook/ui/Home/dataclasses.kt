package com.gaurav.smartcook.ui.Home

import com.google.firebase.firestore.PropertyName

data class userPref(
    var ingredients: String = "",
    var allergyInfo: String = "",
    var diet: String = "",
    var neededNote: String = "",
    var specialNote: String = "",
    var ServeSize: Int = 4
)

data class prevRecipie(
    var id: String = "",
    var name: String = "",
    var image: String = "",
    var cookTime: String ="",
    @get:PropertyName("isFavourite")
    @set:PropertyName("isFavourite")
    var isFavourite: Boolean = false
)