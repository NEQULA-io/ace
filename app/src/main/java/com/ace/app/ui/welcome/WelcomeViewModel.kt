package com.ace.app.ui.welcome

import android.content.Context
import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ace.app.auth.AuthRepository
import com.ace.app.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WelcomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val isBiometricAvailable: Boolean = false
)

class WelcomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authRepository = AuthRepository(application)
    
    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()
    
    init {
        checkBiometricAvailability()
        if (FirebaseAuth.getInstance().currentUser != null) {
            _uiState.value = _uiState.value.copy(isAuthenticated = true)
        }
    }

    private fun checkBiometricAvailability() {
        val isAvailable = authRepository.isBiometricAvailable()
        _uiState.value = _uiState.value.copy(isBiometricAvailable = isAvailable)
    }
    
    fun onGoogleSignIn(activity: FragmentActivity) {
        println("ACE DEBUG: Google button pressed")

        viewModelScope.launch {
            println("ACE DEBUG: Starting Google authentication")

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            println("ACE DEBUG: Calling AuthRepository")

            when (val result = authRepository.authenticateWithGoogle(activity)) {

                is AuthResult.Success -> {
                    println("ACE DEBUG: Firebase authentication SUCCESS")
                    println("ACE DEBUG: User ID = ${result.userId}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }

                is AuthResult.Error -> {
                    println("ACE DEBUG: AUTH ERROR = ${result.message}")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is AuthResult.Cancelled -> {
                    println("ACE DEBUG: Google authentication CANCELLED")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun onBiometricSignIn(activity: FragmentActivity) {
        authRepository.authenticateWithBiometric(activity) { result ->
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        error = null
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message
                    )
                }
                is AuthResult.Cancelled -> {
                    // User cancelled, no action needed
                }
            }
        }
    }

    fun onSignOut(context: Context) {
        viewModelScope.launch {
            authRepository.signOut(context)
            _uiState.value = _uiState.value.copy(isAuthenticated = false)
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
