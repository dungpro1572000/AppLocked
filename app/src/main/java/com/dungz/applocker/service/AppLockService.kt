package com.dungz.applocker.service

import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dungz.applocker.activity.MainActivity
import com.dungz.applocker.R
import com.dungz.applocker.data.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.java

@AndroidEntryPoint
class AppLockService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isMonitoring = false
    private lateinit var usageStatsManager: UsageStatsManager
    private var lastCheckedTime = 0L

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "app_locker_channel"
        private const val CHECK_INTERVAL = 1000L // 1 second
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        lastCheckedTime = System.currentTimeMillis()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_MONITORING" -> startMonitoring()
            "STOP_MONITORING" -> stopMonitoring()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startMonitoring() {
        if (isMonitoring) return

        // Check if we have usage stats permission
        if (!hasUsageStatsPermission()) {
            Log.w("DungNT354", "Usage stats permission not granted")
            // You should prompt user to grant permission
            stopSelf()
            return
        }

        isMonitoring = true
        startForeground(NOTIFICATION_ID, createNotification())

        serviceScope.launch {
            monitorAppUsage()
        }
    }

    private fun stopMonitoring() {
        isMonitoring = false
        stopForeground(true)
        stopSelf()
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private suspend fun monitorAppUsage() {
        while (isMonitoring) {
            try {
                val currentTime = System.currentTimeMillis()
                val foregroundApp = getCurrentForegroundApp()

                if (foregroundApp != null) {
                    Log.d("DungNT354", "Current foreground app: $foregroundApp")

                    // Check if this app is in your locked apps list
                    if (isAppLocked(foregroundApp)) {
                        Log.d("DungNT354", "Locked app detected: $foregroundApp")
                        handleLockedApp(foregroundApp)
                    }
                }

                lastCheckedTime = currentTime
            } catch (e: Exception) {
                Log.e("DungNT354", "Error monitoring app usage", e)
            }

            delay(CHECK_INTERVAL)
        }
    }

    private fun getCurrentForegroundApp(): String? {
        val currentTime = System.currentTimeMillis()

        // Query usage stats for the last 10 seconds
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            currentTime - 10000,
            currentTime
        )

        if (stats.isEmpty()) {
            return null
        }

        // Find the most recently used app
        val mostRecentApp = stats.maxByOrNull { it.lastTimeUsed }

        // Additional check using queryAndAggregateUsageStats for better accuracy
        val recentStats = usageStatsManager.queryAndAggregateUsageStats(
            currentTime - 5000,
            currentTime
        )

        // Get the app with the most recent foreground time
        val currentApp = recentStats.maxByOrNull {
            it.value.lastTimeUsed
        }?.key

        return currentApp ?: mostRecentApp?.packageName
    }

    private fun isAppLocked(packageName: String): Boolean {
        // TODO: Implement your logic to check if app is locked
        // This could be from SharedPreferences, Room database, etc.
        // For example:
        val lockedApps = getLockedApps()
        return lockedApps.contains(packageName)
    }

    private fun getLockedApps(): Set<String> {
        // TODO: Get locked apps from your storage
        // Example implementation:
        val prefs = getSharedPreferences("app_locker", Context.MODE_PRIVATE)
        return prefs.getStringSet("locked_apps", emptySet()) ?: emptySet()
    }

    private fun handleLockedApp(packageName: String) {
        // TODO: Implement your app locking logic
        // This could involve:
        // 1. Showing a lock screen activity
        // 2. Bringing your app to foreground
        // 3. Showing authentication dialog

        Log.d("DungNT354", "Handling locked app: $packageName")

        // Example: Launch lock screen
        val lockIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("locked_package", packageName)
        }
        startActivity(lockIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "App Locker Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "App Locker is monitoring app usage"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Locker Active")
            .setContentText("Monitoring app usage")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        serviceScope.cancel()
    }
}