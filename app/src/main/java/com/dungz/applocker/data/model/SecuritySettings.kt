package com.dungz.applocker.data.model

data class SecuritySettings(
    val isPasswordSet: Boolean = false,
    val password: String = "",
    val emergencyPassword: String = "",
    val isEmergencyPasswordSet: Boolean = false,
    val emergencyUnlockUntil: Long = 0L,
    val failedAttempts: Int = 0,
    val lastFailedAttempt: Long = 0L,
    val isBiometricEnabled: Boolean = false
) 