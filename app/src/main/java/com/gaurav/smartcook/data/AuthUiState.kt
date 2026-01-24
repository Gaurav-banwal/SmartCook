package com.gaurav.smartcook.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf


data class AuthUiState(
    val email: String ,
    val password: String,
    val passwordConfirmation: String,
    val isLoading: Boolean
)