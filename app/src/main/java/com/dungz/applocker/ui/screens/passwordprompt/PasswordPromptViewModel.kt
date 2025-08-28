package com.dungz.applocker.ui.screens.passwordprompt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PasswordPromptViewModel @Inject constructor(
    private val appRepository: AppRepository
) :
    ViewModel() {
    private val _state = MutableStateFlow(PasswordPromptState())
    val state: StateFlow<PasswordPromptState> = _state

    init {
        viewModelScope.launch {
            val settings = appRepository.getSecuritySettings()
            _state.value =
                _state.value.copy(isEmergencyPasswordSet = settings.isEmergencyPasswordSet)
            Log.d("DungNT35444","check state ${_state.value.isEmergencyPasswordSet}")
        }
    }

    fun updatePassword(newPassword: String) {
        _state.value = _state.value.copy(password = newPassword, error = null)
    }

    fun updateError(error: String?) {
        _state.value = _state.value.copy(error = error)
    }

    fun toggleShowPassword() {
        _state.value = _state.value.copy(showPassword = !_state.value.showPassword)
    }

    fun toggleNormalPasswordView() {
        _state.value =
            _state.value.copy(isShowNormalPasswordView = !_state.value.isShowNormalPasswordView)
    }

    fun validatePassword(password: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val isValid =
                if (_state.value.isShowNormalPasswordView) appRepository.validatePassword(password)
                else appRepository.validateEmergencyPassword(
                    password
                )
            if (isValid) {
                _state.value = _state.value.copy(attemptsCount = 0)
                onSuccess()
            } else {
                val currentAttempts = _state.value.attemptsCount
                _state.value = _state.value.copy(attemptsCount = currentAttempts + 1)
                onError()
            }
        }
    }

    fun scheduleEmergencyUnlock() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.unlockAllApps()
            appRepository.scheduleEmergencyUnlock()
        }
    }
}