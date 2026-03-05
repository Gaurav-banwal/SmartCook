package com.gaurav.smartcook.data.remote.firebase


import com.google.firebase.firestore.PropertyName

data class UserProfile(
    @get:PropertyName("Name") @set:PropertyName("Name") var Name: String = "",
    @get:PropertyName("Age") @set:PropertyName("Age") var Age: Int = 0,
    @get:PropertyName("Gender") @set:PropertyName("Gender") var Gender: String = "",
    @get:PropertyName("Diet") @set:PropertyName("Diet") var Diet: String = "",
    @get:PropertyName("Allergy") @set:PropertyName("Allergy") var Allergy: String = "",
    @get:PropertyName("Servesize") @set:PropertyName("Servesize") var Servesize: Int = 1,
    @get:PropertyName("Specialcooknote") @set:PropertyName("Specialcooknote") var Specialcooknote: String = ""
)