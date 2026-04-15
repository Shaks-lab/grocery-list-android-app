package com.example.groceryapp.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val biometricManager = BiometricManager(application.applicationContext)

    private val _biometricStatus = MutableLiveData<BiometricStatus>()
    val biometricStatus: LiveData<BiometricStatus> = _biometricStatus

    private val _isBiometricEnabled = MutableLiveData<Boolean>()
    val isBiometricEnabled: LiveData<Boolean> = _isBiometricEnabled

    init {
        checkBiometricStatus()
        loadBiometricPreference()
    }

    private fun checkBiometricStatus() {
        viewModelScope.launch {
            _biometricStatus.value = biometricManager.isBiometricAvailable()
        }
    }

    private fun loadBiometricPreference() {
        _isBiometricEnabled.value = biometricManager.isBiometricEnabled()
    }

    fun enableBiometricAuthentication(
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit
    ) {
        // This will be called from the composable with the activity context
        // The actual authentication will be triggered from the composable
        setBiometricEnabled(true)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        biometricManager.setBiometricEnabled(enabled)
        _isBiometricEnabled.value = enabled
    }
}