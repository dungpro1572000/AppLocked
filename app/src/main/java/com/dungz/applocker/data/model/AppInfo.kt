package com.dungz.applocker.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable,
    val isSystemApp: Boolean = false,
    val isLocked: Boolean = false,
    val isSelected: Boolean = false
) 