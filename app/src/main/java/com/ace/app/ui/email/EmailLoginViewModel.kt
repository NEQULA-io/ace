package com.ace.app.ui.email

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ace.app.auth.AuthRepository
import com.ace.app.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmailLoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class EmailLoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authRepository = AuthRepository(application)
    
    private val _uiState = MutableStateFlow(EmailLoginUiState())
    val uiState: StateFlow<EmailLoginUiState> = _uiState.asStateFlow()
    
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }
    
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }
    
    fun onSignIn() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.authenticateWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is AuthResult.Cancelled -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }
    
    fun onForgotPassword() {
        val email = _uiState.value.email
        
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter your email address"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.resetPassword(email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Password reset instructions sent to your email"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is AuthResult.Cancelled -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
