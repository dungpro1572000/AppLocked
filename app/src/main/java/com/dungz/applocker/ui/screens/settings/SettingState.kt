package com.dungz.applocker.ui.screens.settings

import com.dungz.applocker.data.model.SecuritySettings

data class SettingState(
    val securitySettings: SecuritySettings = SecuritySettings(),
    val isShowChangePasswordDialog: Boolean = false,
    val isShowEmergencyPasswordDialog: Boolean = false
) {

}