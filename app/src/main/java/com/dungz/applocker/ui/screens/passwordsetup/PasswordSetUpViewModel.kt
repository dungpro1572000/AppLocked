package com.dungz.applocker.ui.screens.passwordsetup

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
class PasswordSetUpViewModel @Inject constructor(val appRepository: AppRepository) : ViewModel() {
    private val _state = MutableStateFlow(PasswordSetUpState("", ""))
    val state: StateFlow<PasswordSetUpState>
        get() = _state

    fun onSetUpPassword() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentSecuritySettings = appRepository.getSecuritySettings()
            val hashedPassword = PasswordHasher.hashPassword(_state.value.password)
            val newSecurity = currentSecuritySettings.copy(
                isPasswordSet = true,
                password = hashedPassword
            )
            appRepository.saveSecuritySettings(newSecurity)
        }
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _state.value= _state.value.copy(confirmPassword = confirmPassword)
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
}