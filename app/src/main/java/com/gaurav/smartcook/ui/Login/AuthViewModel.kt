package com.gaurav.smartcook.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * UI State for Authentication operations
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Login State
    private val _loginstate = MutableStateFlow(AuthUiState())
    val loginstate: StateFlow<AuthUiState> = _loginstate.asStateFlow()

    // Registration State
    private val _registerstate = MutableStateFlow(AuthUiState())
    val registerstate: StateFlow<AuthUiState> = _registerstate.asStateFlow()

    // Password Reset State
    private val _resetstate = MutableStateFlow(AuthUiState())
    val resetstate: StateFlow<AuthUiState> = _resetstate.asStateFlow()







    init {
        // Check if a user session already exists
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // Immediately set the state to success
            _loginstate.value = AuthUiState(user = currentUser, isSuccess = true)
        }
    }


    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginstate.value = AuthUiState(isLoading = true)
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
                _loginstate.value = AuthUiState(user = result.user, isSuccess = true)
            } catch (e: Exception) {
                _loginstate.value = AuthUiState(error = e.localizedMessage ?: "Login failed")
            }
        }
    }


    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _registerstate.value = AuthUiState(isLoading = true)
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                val user = result.user

                // Set the user's name in their Firebase profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user?.updateProfile(profileUpdates)?.await()

                _registerstate.value = AuthUiState(user = user, isSuccess = true)
            } catch (e: Exception) {
                _registerstate.value = AuthUiState(error = e.localizedMessage ?: "Registration failed")
            }
        }
    }


    fun forgetPassword(email: String) {
        viewModelScope.launch {
            _resetstate.value = AuthUiState(isLoading = true)
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                _resetstate.value = AuthUiState(isSuccess = true)
            } catch (e: Exception) {
                _resetstate.value = AuthUiState(error = e.localizedMessage ?: "Failed to send reset email")
            }
        }
    }

    fun googleSignIn(idtoken: String) {
        Log.d("AUTH", "Received token: $idtoken")
        viewModelScope.launch {
            _loginstate.value = AuthUiState(isLoading = true)
            try {
                val credential = GoogleAuthProvider.getCredential(idtoken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                _loginstate.value = AuthUiState(user = result.user, isSuccess = true)
            } catch (e: Exception) {
                _loginstate.value = AuthUiState(error = e.localizedMessage ?: "Google sign in failed")
            }
        }
    }
    fun logout() {
        firebaseAuth.signOut()
        _loginstate.value = AuthUiState(user = null, isSuccess = false)
    }


    fun resetStates() {
        _loginstate.value = AuthUiState()
        _registerstate.value = AuthUiState()
        _resetstate.value = AuthUiState()
    }
}
