package com.dungz.applocker.ui.screens.appselection

import com.dungz.applocker.data.model.AppInfo

data class AppSelectionState(
    val searchApp: String = "",
    val isShowSystemApp: Boolean = false,
    val isShowSystemWindowAlertPermissionDialog: Boolean = false,
    val isShowUsageStatsPermissionDialog: Boolean = false,
    val isShowDenyPermissionDialog: Boolean = false,
    val listLockedApp: List<AppInfo> = emptyList(),
    val listApp: List<AppInfo> = emptyList()
)