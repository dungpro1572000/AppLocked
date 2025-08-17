package com.dungz.applocker.ui.screens.appselection

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.dungz.applocker.data.model.AppInfo

data class AppSelectionState(
    val searchApp: String = "",
    val isShowSystemApp: Boolean = false,
    val isShowSystemWindowAlertPermissionDialog: Boolean = false,
    val isShowUsageStatsPermissionDialog: Boolean = false,
    val isShowDenyPermissionDialog: Boolean = false,
    val listLockedApp: List<AppSelectionInfo> = emptyList(),
    val listApp: List<AppSelectionInfo> = emptyList()
)

data class AppSelectionInfo(
    val packageName: String,
    val appName: String,
    val appIcon: ImageBitmap,
    val isSystemApp: Boolean = false,
    val isLocked: Boolean = false,
    val isSelected: Boolean = false
)