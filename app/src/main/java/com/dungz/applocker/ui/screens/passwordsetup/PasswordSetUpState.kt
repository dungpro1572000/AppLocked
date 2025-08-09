package com.dungz.applocker.ui.screens.passwordsetup

data class PasswordSetUpState(
    val password: String = "",
    val confirmPassword: String = "",
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val error: String? = null
) {
}