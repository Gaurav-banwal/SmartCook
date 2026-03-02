package com.gaurav.smartcook.viewmodel

import android.util.Log
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

/**
 * UI State for Authentication operations
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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

    /**
     * Authenticate user with Email and Password
     */
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

    /**
     * Create a new user account and set the display name
     */
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

    /**
     * Send a password reset link to the provided email
     */
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

    /**
     * Authenticate using a Google ID Token
     */
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

    /**
     * Clear all error and success states (e.g., when switching screens)
     */
    fun resetStates() {
        _loginstate.value = AuthUiState()
        _registerstate.value = AuthUiState()
        _resetstate.value = AuthUiState()
    }
}
