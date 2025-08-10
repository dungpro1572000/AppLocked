package com.dungz.applocker.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.dungz.applocker.data.database.LockedAppDao
import com.dungz.applocker.data.datastore.AppDataStore
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.data.model.LockedApp
import com.dungz.applocker.data.model.SecuritySettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val lockedAppDao: LockedAppDao,
    private val dataStore: AppDataStore,
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
        val data = dataStore.getSecuritySettings()
        return data
    }

    override suspend fun saveSecuritySettings(settings: SecuritySettings) {
        dataStore.saveSecuritySettings(settings)
    }

    override suspend fun deleteAllSecuritySettings() {
        dataStore.deleteDataStore()
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