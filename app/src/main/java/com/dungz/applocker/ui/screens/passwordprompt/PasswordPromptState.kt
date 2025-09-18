package com.dungz.applocker.ui.screens.passwordprompt

data class PasswordPromptState(
    val password: String = "",
    val showPassword: Boolean = false,
    val isShowNormalPasswordView: Boolean = true,
    val isEmergencyPasswordSet: Boolean = false,
    val error: String? = null,
    val attemptsCount: Int = 0,
) {
}