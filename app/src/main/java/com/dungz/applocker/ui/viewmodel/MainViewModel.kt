package com.dungz.applocker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.data.model.SecuritySettings
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.util.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val biometricHelper: BiometricHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _lockedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val lockedApps: StateFlow<List<AppInfo>> = _lockedApps.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load security settings
            val settings = appRepository.getSecuritySettings()
            _uiState.value = _uiState.value.copy(
                securitySettings = settings,
                isPasswordSet = settings.isPasswordSet
            )

            // Load locked apps
            appRepository.getLockedApps().collect { lockedApps ->
                val allApps = appRepository.getAllInstalledApps()
                val updatedApps = allApps.map { app ->
                    app.copy(isLocked = lockedApps.any { it.packageName == app.packageName })
                }
                _lockedApps.value = updatedApps
            }
        }
    }

    fun checkDeviceSecurity(): Boolean {
        return biometricHelper.isBiometricAvailable()
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            val settings = _uiState.value.securitySettings.copy(
                password = password,
                isPasswordSet = true
            )
            appRepository.saveSecuritySettings(settings)
            _uiState.value = _uiState.value.copy(
                securitySettings = settings,
                isPasswordSet = true
            )
        }
    }

    fun setEmergencyPassword(password: String) {
        viewModelScope.launch {
            val settings = _uiState.value.securitySettings.copy(
                emergencyPassword = password,
                isEmergencyPasswordSet = true
            )
            appRepository.saveSecuritySettings(settings)
            _uiState.value = _uiState.value.copy(securitySettings = settings)
        }
    }

    fun validatePassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val isValid = appRepository.validatePassword(password)
            if (isValid) {
                appRepository.resetFailedAttempts()
                onSuccess()
            } else {
                appRepository.incrementFailedAttempts()
                onError()
            }
        }
    }

    fun validateEmergencyPassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val isValid = appRepository.validateEmergencyPassword(password)
            if (isValid) {
                appRepository.setEmergencyUnlock()
                appRepository.resetFailedAttempts()
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun shouldTakePhoto(): Boolean {
        return _uiState.value.securitySettings.failedAttempts >= 3
    }

    fun isEmergencyUnlockActive(): Boolean {
        val settings = _uiState.value.securitySettings
        return settings.emergencyUnlockUntil > System.currentTimeMillis()
    }

    fun refreshApps() {
        val allApps = appRepository.getAllInstalledApps()
        _lockedApps.value = allApps
    }
}

data class MainUiState(
    val securitySettings: SecuritySettings = SecuritySettings(),
    val isPasswordSet: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) 