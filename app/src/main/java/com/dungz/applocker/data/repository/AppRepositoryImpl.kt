package com.dungz.applocker.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
        val packageManager = context.packageManager
        val apps = mutableListOf<AppInfo>()
        try {
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledPackages(0)
            }

            for (packageInfo in packages) {
                try {
                    val applicationInfo = packageInfo.applicationInfo
                    if (applicationInfo != null) {
                        val appName = applicationInfo.loadLabel(packageManager).toString()
                        val packageName = applicationInfo.packageName
                        val icon = applicationInfo.loadIcon(packageManager)

                        val isSystemApp =
                            (applicationInfo.flags.and(ApplicationInfo.FLAG_SYSTEM)) != 0

                        apps.add(
                            AppInfo(
                                packageName = packageName,
                                appName = appName,
                                appIcon = icon,
                                isSystemApp = isSystemApp
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.w(
                        "InstalledApps",
                        "Error processing package: ${packageInfo.packageName}",
                        e
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("InstalledApps", "Error getting installed packages", e)
        }
        return apps.sortedBy { it.appName }
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