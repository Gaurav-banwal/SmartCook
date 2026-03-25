package com.gaurav.smartcook.ui.runrecipie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaurav.smartcook.data.remote.firebase.RecipieFromFirebase
import com.gaurav.smartcook.data.repository.RecipieRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipieSummaryViewModel @Inject constructor(
    private val repository: RecipieRepository
): ViewModel() {



    var recipie by mutableStateOf<RecipieFromFirebase?>(null)
    var isLoading by mutableStateOf(false)
    var isError by mutableStateOf(false)

    fun fetchRecipie(recid: String) {
        if (recid.isEmpty()) return

        viewModelScope.launch {

            isLoading = true
            isError = false

            val res = repository.getRecipieBYId(recid)

            if(res != null){
                recipie = res
            }else {
                isError = true
            }
            isLoading = false

        }


    }


}
