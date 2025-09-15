package com.dungz.applocker.ui.screens.settings

import android.app.ProgressDialog.show
import android.util.Log
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


    fun updateShowChangePasswordDialog() {
        val value = _state.value.isShowChangePasswordDialog
        _state.value = _state.value.copy(isShowChangePasswordDialog = !value)
    }

    fun updateShowEmergencyPasswordDialog() {
        val value = _state.value.isShowEmergencyPasswordDialog
        _state.value = _state.value.copy(isShowEmergencyPasswordDialog = !value)
    }
    fun validatePassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val isValid =
                 appRepository.validatePassword(password)
            Log.d("DungNT35444","check isValid inputPassword:${password} $isValid")
            if (isValid) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
    fun validateEmergencyPassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
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