package com.dungz.applocker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_apps")
data class LockedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isLocked: Boolean = true,
    val lockDate: Long = System.currentTimeMillis()
) 