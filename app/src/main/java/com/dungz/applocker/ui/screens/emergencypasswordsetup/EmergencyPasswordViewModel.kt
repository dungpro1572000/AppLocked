package com.dungz.applocker.ui.screens.emergencypasswordsetup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EmergencyPasswordViewModel @Inject constructor(private val appRepository: AppRepository) :
    ViewModel() {
    private val _state = MutableStateFlow(EmergencyPasswordSetupState())
    val state: StateFlow<EmergencyPasswordSetupState> = _state

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword)
    }

    fun updateShowPassword(show: Boolean) {
        _state.value = _state.value.copy(showPassword = show)
    }

    fun updateShowConfirmPassword(show: Boolean) {
        _state.value = _state.value.copy(showConfirmPassword = show)
    }

    fun updateError(error: String?) {
        _state.value = _state.value.copy(error = error)
    }

    fun updateEmergencyPassword() {
        // Logic to update the emergency password in the repository
        // This could involve saving the password securely
        viewModelScope.launch {
            val currentSettings = appRepository.getSecuritySettings()
            val updatedSettings = currentSettings.copy(
                emergencyPassword = _state.value.password,
                isEmergencyPasswordSet = true
            )
            appRepository.saveSecuritySettings(updatedSettings)
        }
    }
}