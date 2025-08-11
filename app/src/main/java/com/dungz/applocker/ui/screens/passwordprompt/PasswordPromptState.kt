package com.dungz.applocker.ui.screens.passwordprompt

data class PasswordPromptState(
    val password: String = "",
    val showPassword: Boolean = true,
    val isShowNormalPasswordView: Boolean = true,
    val error: String? = null,
    val attemptsCount: Int = 0,
) {
}