package com.dungz.applocker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp_locked_apps")
data class TempLockedApp (
    @PrimaryKey
    val packageName: String,
    val appName: String,
)