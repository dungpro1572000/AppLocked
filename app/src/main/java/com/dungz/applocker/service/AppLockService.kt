package com.dungz.applocker.service

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.dungz.applocker.R
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptScreen
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptViewModel
import com.dungz.applocker.ui.theme.AppLockerTheme
import com.dungz.applocker.ui.theme.LocalAppColorScheme
import com.dungz.applocker.ui.theme.Typography
import com.dungz.applocker.util.ToastLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class AppLockService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isMonitoring = false
    private lateinit var usageStatsManager: UsageStatsManager
    private var lastCheckedTime = 0L
    val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        },
        // Các cờ để cửa sổ có thể nhận sự kiện chạm và hiển thị toàn màn hình
        // Loại bỏ cờ NOT_FOCUSABLE để màn hình có thể tương tác
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )
    var overlayView: ComposeView? = null
    var windowManager: WindowManager? = null
    val overlayLifecycleOwner = ToastLifecycleOwner()

    @Inject
    lateinit var appRepository: AppRepository

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "app_locker_channel"
        private const val CHECK_INTERVAL = 750L // 1 second
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        Log.d("DungNT354", "AppLockService created")
        lastCheckedTime = System.currentTimeMillis()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_MONITORING" -> startMonitoring()
            "STOP_MONITORING" -> stopMonitoring()
        }
        Log.d("DungNT354", "AppLockService onStartCommand called with action: ${intent?.action}")
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
        stopForeground(STOP_FOREGROUND_REMOVE)
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
                val foregroundApp = getForegroundApp(context = this)
                Log.d("DungNT354", "Foreground app at $currentTime: $foregroundApp")

                if (foregroundApp != null) {
                    Log.d("DungNT354", "Current foreground app: $foregroundApp")

                    // Check if this app is in your locked apps list
//                    if (isAppLocked(foregroundApp)) {
//                        Log.d("DungNT354", "Locked app detected: $foregroundApp")
//                        handleLockedApp(foregroundApp)
//                    }

                    if (foregroundApp == "com.google.android.youtube") {
                        handleLockedApp(foregroundApp)
                    } else {
                        withContext(Dispatchers.Main) {
                            overlayView?.let {
                                windowManager?.removeView(overlayView)
                                windowManager = null
                            }
                        }
                    }
                }

                lastCheckedTime = currentTime
            } catch (e: Exception) {
                Log.e("DungNT354", "Error monitoring app usage", e)
            }

            delay(CHECK_INTERVAL)
        }
    }

    fun getForegroundApp(context: Context): String? {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        // Lấy thống kê trong 1 phút gần nhất
        val appList = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - 1000 * 60,
            time
        )

        if (appList.isNullOrEmpty()) {
            return null
        }

        // Sắp xếp theo lastTimeUsed
        val recentApp = appList.maxByOrNull { it.lastTimeUsed }

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.runningAppProcesses
        val foregroundApp = tasks?.firstOrNull {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }?.processName

        return recentApp?.packageName.toString()
    }

    private suspend fun isAppLocked(packageName: String): Boolean {
        // TODO: Implement your logic to check if app is locked
        // This could be from SharedPreferences, Room database, etc.
        // For example:
        val lockedApps = getLockedApps()
        return lockedApps.contains(packageName)
    }

    private suspend fun getLockedApps(): List<String> {

        return appRepository.getLockedApps().first().map {
            it.packageName
        }
    }

    private suspend fun handleLockedApp(packageName: String) {
        withContext(Dispatchers.Main) {
            if (windowManager == null) {
                windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                createAndAddOverlayView()
                Log.d("DungNT354", "WindowManager initialized in handleLockedApp")
            }
            Log.d("DungNT354", "Handling locked app: $packageName")
        }
    }

    private fun createAndAddOverlayView() {
        if (overlayView == null) {
            overlayLifecycleOwner.performRestore(null)
            overlayLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            val viewModelStoreOwner = object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore
                    get() = ViewModelStore()
            }
            val viewModel = PasswordPromptViewModel(appRepository = appRepository)
            overlayView = ComposeView(this).apply {
                setContent {
                        AppLockerTheme(
                            content = {
                                PasswordPromptScreen(
                                    onSuccess = {
                                        Log.d("DungNT354", "Password prompt success")
                                        removeOverlayView()
                                    },
                                    onEmergencyUnLock = { /* TODO: Implement */ },
                                    viewModel = viewModel
                                )
                            }
                        )
                }
                setViewTreeLifecycleOwner(overlayLifecycleOwner)
                setViewTreeSavedStateRegistryOwner(overlayLifecycleOwner)
            }

            // Correctly manage the lifecycle of the overlay

            overlayLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            overlayLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        try {
            windowManager?.addView(overlayView, params)
            Log.d("DungNT354", "windowManager is null? ${windowManager == null}")
        } catch (e: Exception) {
            Log.e("AppLockService", "Error adding view to window manager", e)
        }
    }

    private fun removeOverlayView() {
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
                // Correct lifecycle cleanup
                overlayLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                overlayLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                overlayLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

                // Clear the reference
                overlayView = null
                windowManager = null
                Log.d("AppLockService", "Overlay view removed")
            } catch (e: Exception) {
                Log.e("AppLockService", "Error removing view from window manager", e)
            }
        }
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
        windowManager = null
        overlayView = null
        serviceScope.cancel()
    }
}