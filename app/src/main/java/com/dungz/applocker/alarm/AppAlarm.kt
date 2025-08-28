package com.dungz.applocker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.service.AppMonitorService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppAlarm @Inject constructor(
    private val appRepository: AppRepository,
    @ApplicationContext private val context: Context,
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    suspend fun setAlarmForOpenLockedApps(
        appName: String,
        packageName: String,
        timeTrigger: Int,
    ) {
        appRepository.unlockApp(packageName)
        val intent = Intent(context, AppMonitorService::class.java).apply {
            action = ACTION_LOCK_APP
            putExtra(APP_NAME_EXTRA, appName)
            putExtra(PACKAGE_NAME_EXTRA, packageName)
        }

        val pendingIntent = PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            System.currentTimeMillis() + timeTrigger * 1000 * 60, // Convert minutes to milliseconds
            pendingIntent
        )

    }

    companion object {
        const val ACTION_LOCK_APP = "com.dungz.applocker.action.LOCK_APP"
        const val APP_NAME_EXTRA = "app_name_extra"
        const val PACKAGE_NAME_EXTRA = "package_name_extra"
    }
}