package com.dungz.applocker.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.dungz.applocker.data.database.LockedAppDao
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.data.model.LockedApp
import com.dungz.applocker.data.model.SecuritySettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "security_settings")

@Singleton
class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lockedAppDao: LockedAppDao
) : AppRepository {

    private val packageManager: PackageManager = context.packageManager

    override fun getAllInstalledApps(): List<AppInfo> {
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return installedApps.mapNotNull { appInfo ->
            try {
                val appName = appInfo.loadLabel(packageManager).toString()
                val appIcon = appInfo.loadIcon(packageManager)
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = appName,
                    appIcon = appIcon,
                    isSystemApp = isSystemApp
                )
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.appName }
    }

    override fun getLockedApps(): Flow<List<LockedApp>> {
        return lockedAppDao.getAllLockedApps()
    }

    override suspend fun isAppLocked(packageName: String): Boolean {
        return lockedAppDao.isAppLocked(packageName)
    }

    override suspend fun lockApp(appInfo: AppInfo) {
        val lockedApp = LockedApp(
            packageName = appInfo.packageName,
            appName = appInfo.appName
        )
        lockedAppDao.insertLockedApp(lockedApp)
    }

    override suspend fun unlockApp(packageName: String) {
        lockedAppDao.deleteLockedAppByPackage(packageName)
    }

    override suspend fun unlockAllApps() {
        lockedAppDao.unlockAllApps()
    }

    override suspend fun getSecuritySettings(): SecuritySettings {
        val preferences = context.dataStore.data.first()
        return SecuritySettings(
            isPasswordSet = preferences[booleanPreferencesKey("is_password_set")] ?: false,
            password = preferences[stringPreferencesKey("password")] ?: "",
            emergencyPassword = preferences[stringPreferencesKey("emergency_password")] ?: "",
            isEmergencyPasswordSet = preferences[booleanPreferencesKey("is_emergency_password_set")] ?: false,
            emergencyUnlockUntil = preferences[longPreferencesKey("emergency_unlock_until")] ?: 0L,
            failedAttempts = preferences[intPreferencesKey("failed_attempts")] ?: 0,
            lastFailedAttempt = preferences[longPreferencesKey("last_failed_attempt")] ?: 0L,
            isBiometricEnabled = preferences[booleanPreferencesKey("is_biometric_enabled")] ?: false
        )
    }

    override suspend fun saveSecuritySettings(settings: SecuritySettings) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("is_password_set")] = settings.isPasswordSet
            preferences[stringPreferencesKey("password")] = settings.password
            preferences[stringPreferencesKey("emergency_password")] = settings.emergencyPassword
            preferences[booleanPreferencesKey("is_emergency_password_set")] = settings.isEmergencyPasswordSet
            preferences[longPreferencesKey("emergency_unlock_until")] = settings.emergencyUnlockUntil
            preferences[intPreferencesKey("failed_attempts")] = settings.failedAttempts
            preferences[longPreferencesKey("last_failed_attempt")] = settings.lastFailedAttempt
            preferences[booleanPreferencesKey("is_biometric_enabled")] = settings.isBiometricEnabled
        }
    }

    override suspend fun validatePassword(password: String): Boolean {
        val settings = getSecuritySettings()
        return settings.password == password
    }

    override suspend fun validateEmergencyPassword(password: String): Boolean {
        val settings = getSecuritySettings()
        return settings.emergencyPassword == password
    }

    override suspend fun setEmergencyUnlock() {
        val settings = getSecuritySettings()
        val newSettings = settings.copy(
            emergencyUnlockUntil = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
        )
        saveSecuritySettings(newSettings)
    }

    override suspend fun incrementFailedAttempts() {
        val settings = getSecuritySettings()
        val newSettings = settings.copy(
            failedAttempts = settings.failedAttempts + 1,
            lastFailedAttempt = System.currentTimeMillis()
        )
        saveSecuritySettings(newSettings)
    }

    override suspend fun resetFailedAttempts() {
        val settings = getSecuritySettings()
        val newSettings = settings.copy(
            failedAttempts = 0,
            lastFailedAttempt = 0L
        )
        saveSecuritySettings(newSettings)
    }

    override suspend fun shouldTakePhoto(): Boolean {
        val settings = getSecuritySettings()
        return settings.failedAttempts >= 3
    }
} 