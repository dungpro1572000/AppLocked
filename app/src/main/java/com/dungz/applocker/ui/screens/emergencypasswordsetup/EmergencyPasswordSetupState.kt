package com.dungz.applocker.ui.screens.emergencypasswordsetup

data class EmergencyPasswordSetupState(
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null,
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false
) {
}