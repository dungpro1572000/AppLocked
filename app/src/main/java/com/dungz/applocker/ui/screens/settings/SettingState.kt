package com.dungz.applocker.ui.screens.settings

import com.dungz.applocker.data.model.SecuritySettings

data class SettingState(
    val securitySettings: SecuritySettings = SecuritySettings(),
    val isShowChangePasswordDialog: Boolean = false,
    val isShowInputPasswordChangePasswordDialog: Boolean = false,
    val isShowInputPasswordEmergencyPasswordDialog: Boolean = false,
    val isShowInputClearAllDataDialog: Boolean = false,
    val isShowEmergencyPasswordDialog: Boolean = false,
    val isShowClearAllDataDialog: Boolean = false,
    val isShowUnlockAllAppDialog: Boolean = false
)