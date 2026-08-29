package com.ace.app.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

sealed class BiometricAuthResult {
    object Success : BiometricAuthResult()
    data class Error(val message: String) : BiometricAuthResult()
    object Unavailable : BiometricAuthResult()
    object NotEnrolled : BiometricAuthResult()
}

class BiometricAuthClient(private val context: Context) {
    
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    fun isBiometricEnrolled(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }
    
    fun authenticate(
        activity: FragmentActivity,
        onResult: (BiometricAuthResult) -> Unit
    ) {
        if (!isBiometricAvailable()) {
            onResult(BiometricAuthResult.Unavailable)
            return
        }
        
        if (!isBiometricEnrolled()) {
            onResult(BiometricAuthResult.NotEnrolled)
            return
        }
        
        val executor = ContextCompat.getMainExecutor(context)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(BiometricAuthResult.Success)
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onResult(BiometricAuthResult.Error(errString.toString()))
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onResult(BiometricAuthResult.Error("Authentication failed"))
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sign in to ACE")
            .setSubtitle("Use your fingerprint to continue")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
}
