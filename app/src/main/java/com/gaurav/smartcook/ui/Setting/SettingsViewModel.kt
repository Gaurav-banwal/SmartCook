package com.gaurav.smartcook.ui.Setting


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaurav.smartcook.data.remote.firebase.UserProfile
import com.gaurav.smartcook.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
   private val userRepository: ProfileRepository
)  : ViewModel() {


    var userProfile = mutableStateOf<UserProfile?>(null)
    var isLoading = mutableStateOf(false)
    var isUpdating = mutableStateOf(false)


    //ui

    var name by mutableStateOf("")
    var age by mutableStateOf("")
    var gender by mutableStateOf("")
    var diet by mutableStateOf("")
    var servesize by mutableFloatStateOf(1f)
    var allergy by mutableStateOf("")
    var specialNote by mutableStateOf("")


    fun loadProfileIntoState(profile: UserProfile) {
        name = profile.Name
        age = profile.Age.toString()
        gender = profile.Gender
        diet = profile.Diet
        servesize = profile.Servesize.toFloat()
        allergy = profile.Allergy
        specialNote = profile.Specialcooknote
    }

    val usremail: String get() = if(userRepository.getUserEmail()!=null) userRepository.getUserEmail()!! else ""


    init {
        fetchUserDataFromAuth()
    }

    //getting error because of no internet so using try catch block
    fun fetchUserDataFromAuth() {

        viewModelScope.launch {
            try {
                isLoading.value = true

                val profile = userRepository.getProfile()
                if (profile != null) {
                    userProfile.value = profile
                    loadProfileIntoState(profile)
                }
            }catch (e: Exception){
              //need to DO
            }finally {
                isLoading.value = false
            }


        }

    }

    fun updateUserData(updatedProfile: UserProfile, onComplete: (Boolean) -> Unit) {


     viewModelScope.launch {
         try {
             isUpdating.value = true
             val profile = userRepository.saveProfile(updatedProfile)

             if (profile) {
                 userProfile.value = updatedProfile
             }
             isUpdating.value = false
             onComplete(profile)
         } catch (e: Exception) {

             onComplete(false)
         }
         finally {
            isUpdating.value = false
         }


        }
    }

    fun logout(){
        userRepository.logout()
    }
}