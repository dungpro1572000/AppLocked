package com.dungz.applocker.data.repository

import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.data.model.LockedApp
import com.dungz.applocker.data.model.SecuritySettings
import com.dungz.applocker.data.model.TempLockedApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getAllInstalledApps(): List<AppInfo>
    fun getLockedApps(): Flow<List<LockedApp>>
    suspend fun isAppLocked(packageName: String): Boolean
    suspend fun lockApp(packageName: String, appName: String)
    suspend fun unlockApp(packageName: String)
    suspend fun unlockAllApps()
    suspend fun getSecuritySettings(): SecuritySettings
    suspend fun saveSecuritySettings(settings: SecuritySettings)
    suspend fun deleteAllSecuritySettings()
    suspend fun validatePassword(password: String): Boolean
    suspend fun validateEmergencyPassword(password: String): Boolean
    suspend fun setEmergencyUnlock()
    suspend fun incrementFailedAttempts()
    suspend fun resetFailedAttempts()
    suspend fun shouldTakePhoto(): Boolean
    suspend fun getTempLockedApps(): List<TempLockedApp>
    suspend fun insertTempLockedApp(tempLockedApps: List<TempLockedApp>)
    fun scheduleEmergencyUnlock()
} 