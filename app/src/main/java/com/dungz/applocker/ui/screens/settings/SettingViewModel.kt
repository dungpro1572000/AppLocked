package com.dungz.applocker.ui.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.util.PasswordHasher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val appRepository: AppRepository) : ViewModel() {
    private val _state = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = appRepository.getSecuritySettings()
            _state.value = _state.value.copy(
                securitySettings = settings,
            )
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val hashedPassword = PasswordHasher.hashPassword(password)
            val settings = _state.value.securitySettings.copy(
                password = hashedPassword,
                isPasswordSet = true
            )
            appRepository.saveSecuritySettings(settings)
            _state.value = _state.value.copy(securitySettings = settings)
        }
    }

    fun unlockAllApps() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.unlockAllApps()
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.unlockAllApps()
            appRepository.deleteAllSecuritySettings()
        }
    }


    fun updateShowChangePasswordDialog() {
        val value = _state.value.isShowChangePasswordDialog
        _state.value = _state.value.copy(isShowChangePasswordDialog = !value)
    }

    fun updateShowEmergencyPasswordDialog() {
        val value = _state.value.isShowEmergencyPasswordDialog
        _state.value = _state.value.copy(isShowEmergencyPasswordDialog = !value)
    }
    fun validatePassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isValid = appRepository.validatePassword(password)
            Log.d("Settings", "Password validation result: $isValid")
            if (isValid) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
    fun validateEmergencyPassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val isValid = appRepository.validateEmergencyPassword(password)
            if (isValid) {
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun updateShowInputPasswordChangePasswordDialog(isShow: Boolean) {
        _state.value = _state.value.copy(isShowInputPasswordChangePasswordDialog = isShow)
    }

    fun updateShowInputPasswordEmergencyPasswordDialog(isShow: Boolean) {
        _state.value = _state.value.copy(isShowInputPasswordEmergencyPasswordDialog = isShow)
    }

    fun updateShowClearAllDataDialog() {
        val value = _state.value.isShowClearAllDataDialog
        _state.value = _state.value.copy(isShowClearAllDataDialog = !value)
    }

    fun updateShowUnlockAllAppDialog() {
        val value = _state.value.isShowUnlockAllAppDialog
        _state.value = _state.value.copy(isShowUnlockAllAppDialog = !value)
    }

    fun updateShowInputClearAllDataConfirmationDialog(isShow: Boolean) {
        _state.value = _state.value.copy(isShowInputClearAllDataDialog = isShow)
    }

}