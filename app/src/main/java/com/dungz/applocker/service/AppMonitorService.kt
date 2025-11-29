package com.dungz.applocker.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.dungz.applocker.activity.MainActivity
import com.dungz.applocker.alarm.AppAlarm
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptScreen
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptViewModel
import com.dungz.applocker.ui.theme.AppLockerTheme
import com.dungz.applocker.util.ToastLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class AppMonitorService : Service() {

    @Inject
    lateinit var appRepository: AppRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var monitoringJob: Job? = null
    private lateinit var usageStatsManager: UsageStatsManager
    private val mutex = Mutex()

    // State management
    private var lastAppPackage = ""
    private var lastCheckTime = 0L
    private var isOverlayShowing = false

    // UI components
    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private var toastLifecycleOwner: ToastLifecycleOwner? = null

    // Cached locked apps for performance
    private var cachedLockedApps = setOf<String>()

    private val overlayParams by lazy {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 0
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "AppLockChannel"
        private const val CHECK_INTERVAL = 500L
        private const val USAGE_STATS_TIME_WINDOW = 10_000L
        private const val USAGE_STATS_FALLBACK_WINDOW = 60_000L
        private const val TAG = "AppMonitorService"
    }

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    private fun initializeService() {
        usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        createNotificationChannel()
        lastCheckTime = System.currentTimeMillis()
        Log.d(TAG, "Service initialized")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())

        handleIntent(intent)
        startMonitoring()

        return START_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == AppAlarm.ACTION_LOCK_APP) {

            val packageName = intent.getStringExtra(AppAlarm.PACKAGE_NAME_EXTRA) ?: ""
            val appName = intent.getStringExtra(AppAlarm.APP_NAME_EXTRA) ?: ""
            Log.d(TAG,"get locked app: $appName")
            if (packageName.isNotEmpty() && appName.isNotEmpty()) {
                serviceScope.launch(Dispatchers.IO) {
                    try {
                        appRepository.lockApp(packageName, appName)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error locking app: $packageName", e)
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    private fun startMonitoring() {
        stopMonitoring()
        stopMonitoring()
        Log.w(TAG, "Monitoring started")

        // Loop 1: log service vẫn sống
        serviceScope.launch {
            repeat(10000) {
                delay(1000L)
                Log.e(TAG, "Service is running... $it and $isActive")
            }
        }

        // Loop 2: monitor foreground app
        serviceScope.launch {
            while (isActive) {
                try {
                    checkAndHandleForegroundApp()
                    delay(CHECK_INTERVAL)
                } catch (e: CancellationException) {
                    Log.w(TAG, "Monitoring loop cancelled", e)
                    throw e // phải rethrow để không nuốt cancel
                } catch (t: Throwable) {
                    Log.e(TAG, "monitor tick error", t)
                    delay(1000) // tránh loop error quá nhanh
                }
            }
        }

        // Loop 3: collect locked apps
        serviceScope.launch {
            while (isActive) {
                try {
                    appRepository.getLockedApps().collect { lockedApps ->
                        cachedLockedApps = lockedApps.map { it.packageName }.toSet()
                        Log.d(TAG, "cachedLockedApps: $cachedLockedApps")
                    }
                } catch (e: CancellationException) {
                    Log.w(TAG, "LockedApps flow cancelled", e)
                } catch (t: Throwable) {
                    Log.e(TAG, "Error in locked apps flow", t)
                    delay(1000)
                }
            }
        }
    }

    private suspend fun checkAndHandleForegroundApp() {
        try {
            val currentApp = getCurrentForegroundApp()

            if (currentApp.isNotEmpty() && currentApp != lastAppPackage) {
                lastAppPackage = currentApp
                handleAppChange(currentApp)
            }

            Log.d(TAG, "Current app: $currentApp")
        } catch (e: Exception) {
            Log.e(TAG, "Error checking foreground app", e)
        }
    }

    private suspend fun handleAppChange(packageName: String) {
        // Skip if it's our own app
        if (packageName == this.packageName) {
            return
        }

        val shouldShowOverlay = cachedLockedApps.contains(packageName)

        mutex.withLock {
            if (shouldShowOverlay && !isOverlayShowing) {
                isOverlayShowing = true
                withContext(Dispatchers.Main) {
                    showLockScreen(packageName)
                }
            } else if (!shouldShowOverlay && isOverlayShowing) {
                isOverlayShowing = false
                withContext(Dispatchers.Main) {
                    hideOverlay()
                }
            }
        }
    }

    private fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        Log.d(TAG, "Monitoring stopped")
    }

    private fun getCurrentForegroundApp(): String {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - USAGE_STATS_TIME_WINDOW

        return try {
            val usageEvents = usageStatsManager.queryEvents(startTime, currentTime)
            var lastAppPackage = ""
            var lastEventTime = 0L

            val event = UsageEvents.Event()
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)

                if (isRelevantUsageEvent(event) && event.timeStamp > lastEventTime) {
                    lastEventTime = event.timeStamp
                    lastAppPackage = event.packageName ?: ""
                }
            }

            lastAppPackage.ifEmpty { getForegroundAppFallback() }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current foreground app", e)
            getForegroundAppFallback()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun isRelevantUsageEvent(event: UsageEvents.Event): Boolean {
        return event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
    }

    private fun getForegroundAppFallback(): String {
        return try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    getForegroundAppFromUsageStats()
                }

                else -> {
                    getForegroundAppFromActivityManager()
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for foreground app detection", e)
            ""
        } catch (e: Exception) {
            Log.e(TAG, "Error getting foreground app fallback", e)
            ""
        }
    }

    private fun getForegroundAppFromUsageStats(): String {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - USAGE_STATS_FALLBACK_WINDOW

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startTime,
            currentTime
        )

        return if (usageStats.isNotEmpty()) {
            usageStats.maxByOrNull { it.lastTimeUsed }?.packageName ?: ""
        } else {
            ""
        }
    }

    private fun getForegroundAppFromActivityManager(): String {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = activityManager.runningAppProcesses

        return runningApps?.find { processInfo ->
            processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }?.processName ?: ""
    }

    private fun hideOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
                Log.d(TAG, "Overlay hidden successfully")
            }
            cleanupOverlayResources()
            serviceScope.launch {
                mutex.withLock { isOverlayShowing = false }
            }
            // ép tick sau khi đóng overlay để nhận app mới ngay
            lastAppPackage = ""
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding overlay", e)
        }
    }

    private fun showLockScreen(packageName: String) {
        Log.d(TAG, "Showing lock screen for package: $packageName")

        try {
            if (overlayView == null) {
                createOverlayWindow()
            }

            overlayView?.let { view ->
                if (view.parent == null) {
                    windowManager?.addView(view, overlayParams)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing lock screen", e)
            cleanupOverlayResources()
        }
    }

    private fun createOverlayWindow() {
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            // Clean up previous lifecycle owner
            toastLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

            // Create new lifecycle owner
            toastLifecycleOwner = ToastLifecycleOwner().apply {
                performRestore(null)
                handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }

            overlayView = ComposeView(this).apply {
                setContent {
                    toastLifecycleOwner?.let { lifecycleOwner ->
                        CompositionLocalProvider(
                            LocalViewModelStoreOwner provides lifecycleOwner
                        ) {
                            val viewModel = viewModel<PasswordPromptViewModel>(
                                factory = createPasswordPromptViewModelFactory()
                            )

                            AppLockerTheme {
                                PasswordPromptScreen(
                                    onSuccess = {
                                        Log.d(TAG, "Password prompt success")
                                        removeLockOverlay()
                                    },
                                    onEmergencyUnLock = {
                                        Log.d(TAG, "Emergency unlock requested")
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }

                setViewTreeLifecycleOwner(toastLifecycleOwner)
                setViewTreeSavedStateRegistryOwner(toastLifecycleOwner)
            }

            toastLifecycleOwner?.let { lifecycleOwner ->
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error creating overlay window", e)
            cleanupOverlayResources()
        }
    }

    private fun createPasswordPromptViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PasswordPromptViewModel(appRepository) as T
            }
        }
    }

    private fun removeLockOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
                Log.d(TAG, "Lock overlay removed successfully")
            }
            cleanupOverlayResources()
//

                isOverlayShowing = false

        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay", e)
        }
    }

    private fun cleanupOverlayResources() {
        overlayView = null
        windowManager = null

        toastLifecycleOwner?.let { lifecycleOwner ->
            try {
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            } catch (e: Exception) {
                Log.e(TAG, "Error destroying lifecycle owner", e)
            }
        }
        toastLifecycleOwner = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Lock Protection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Dịch vụ bảo vệ ứng dụng đang hoạt động"
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Lock đang hoạt động")
            .setContentText("Bảo vệ ${getLockedAppsCount()} ứng dụng")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock) // Add proper icon
            .build()
    }

    private fun getLockedAppsCount(): Int {
        return try {
            cachedLockedApps.size.takeIf { it > 0 } ?: run {
                // Fallback to SharedPreferences if cache is empty
                val sharedPrefs = getSharedPreferences("app_lock_prefs", MODE_PRIVATE)
                val lockedApps =
                    sharedPrefs.getStringSet("locked_apps", emptySet()) ?: emptySet()
                lockedApps.size
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting locked apps count", e)
            0
        }
    }

    override fun onDestroy() {
        Log.e(TAG, "Service destroying")

        try {
            stopMonitoring()
            cleanupOverlayResources()
            serviceScope.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error during service destruction", e)
        } finally {
            super.onDestroy()
        }
    }

    override fun onTimeout(startId: Int) {
        super.onTimeout(startId)
        Log.w(TAG, "Service timeout for startId: $startId")
        // start service again if needed
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        try {
            // Restart service when task is removed
            val restartIntent = Intent(this, AppMonitorService::class.java)
            startForegroundService(restartIntent)
            Log.d(TAG, "Service restarted after task removal")
        } catch (e: Exception) {
            Log.e(TAG, "Error restarting service after task removal", e)
        }
    }
}