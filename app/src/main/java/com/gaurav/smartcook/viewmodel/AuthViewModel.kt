package com.gaurav.smartcook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaurav.smartcook.data.repository.AuthRepository
import com.gaurav.smartcook.data.repository.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val _loginstate = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginstate: MutableStateFlow<Resource<FirebaseUser>?> = _loginstate

    val currentUser: FirebaseUser?
        get() = repository.currentUser


    //fun login(email: String, password: String): Resource<FirebaseUser>
    fun login( email: String, password: String){
        viewModelScope.launch {
            _loginstate.value = Resource.Loading()
            val result = repository.login(email, password)
            _loginstate.value = result

        }
    }

    fun googleSignIn(email: String, password: String){
        viewModelScope.launch {
            _loginstate.value = Resource.Loading()

                val result = repository.googleSignIn(email, password)
            _loginstate.value = result

        }
    }

    //suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser>
    fun signup(name: String, email: String, password: String){
        viewModelScope.launch {
            _loginstate.value = Resource.Loading()
            val result = repository.signup(name, email, password)
            _loginstate.value = result

        }
    }

    fun forgetPassword(email: String){

        viewModelScope.launch {
            _loginstate.value = Resource.Loading()
            val result = repository.forgetPassword(email)
            _loginstate.value = result
        }
    }

    fun logout(){
        repository.logout()
        _loginstate.value = null
    }





   // private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
}