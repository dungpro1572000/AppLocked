package com.dungz.applocker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {
    private val _state = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> = _state

    init {
        viewModelScope.launch {
            val settings = appRepository.getSecuritySettings()
            _state.value = _state.value.copy(
                securitySettings = settings,
            )
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            val settings = _state.value.securitySettings.copy(
                password = password,
                isPasswordSet = true
            )
            appRepository.saveSecuritySettings(settings)
            _state.value = _state.value.copy(securitySettings = settings)
        }
    }

    fun updateEmergencyPassword(emergencyPassword: String) {
        viewModelScope.launch {
            val settings = _state.value.securitySettings.copy(
                emergencyPassword = emergencyPassword,
                isEmergencyPasswordSet = true
            )
            appRepository.saveSecuritySettings(settings)
            _state.value = _state.value.copy(securitySettings = settings)
        }
    }

    fun unLockAllApps() {
        viewModelScope.launch {
            appRepository.unlockAllApps()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            appRepository.unlockAllApps()
            appRepository.deleteAllSecuritySettings()
        }
    }

    fun updateSecuritySettings(settings: SettingState) {
        viewModelScope.launch {
            appRepository.saveSecuritySettings(settings.securitySettings)
            _state.value = settings
        }
    }


    fun updateShowChangePasswordDialog(show: Boolean) {
        _state.value = _state.value.copy(isShowChangePasswordDialog = show)
    }

    fun updateShowEmergencyPasswordDialog(show: Boolean) {
        _state.value = _state.value.copy(isShowEmergencyPasswordDialog = show)
    }
}