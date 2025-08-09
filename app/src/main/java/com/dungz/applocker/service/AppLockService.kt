package com.dungz.applocker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
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

@AndroidEntryPoint
class AppLockService : Service() {

    @Inject
    lateinit var appRepository: AppRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isMonitoring = false

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "app_locker_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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

    private suspend fun monitorAppUsage() {
        while (isMonitoring) {
            // This is a simplified implementation
            // In a real app, you would use UsageStatsManager to monitor app usage
            // and intercept app launches when they are locked
            
            delay(1000) // Check every second
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
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
} 