package com.dungz.applocker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.service.AppMonitorService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppAlarm @Inject constructor(
    private val appRepository: AppRepository,
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun setAlarmForOpenLockedApps(
        appName: String,
        packageName: String,
        timeTriggerMinutes: Int,
    ) {
        appRepository.unlockApp(packageName)
        val intent = Intent(context, AppMonitorService::class.java).apply {
            action = ACTION_LOCK_APP
            putExtra(APP_NAME_EXTRA, appName)
            putExtra(PACKAGE_NAME_EXTRA, packageName)
        }

        // Use unique request code based on package name hash to support multiple alarms
        val requestCode = packageName.hashCode()
        val pendingIntent = PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ELAPSED_REALTIME requires time since boot, so add current elapsed time
        val triggerAtMillis = SystemClock.elapsedRealtime() + (timeTriggerMinutes * 60 * 1000L)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    companion object {
        const val ACTION_LOCK_APP = "com.dungz.applocker.action.LOCK_APP"
        const val APP_NAME_EXTRA = "app_name_extra"
        const val PACKAGE_NAME_EXTRA = "package_name_extra"
    }
}