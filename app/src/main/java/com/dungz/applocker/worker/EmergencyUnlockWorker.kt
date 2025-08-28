package com.dungz.applocker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.dungz.applocker.data.repository.AppRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class EmergencyUnlockWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appRepository: AppRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val listTempApps = appRepository.getTempLockedApps()
            // Reset emergency unlock period
            listTempApps.forEach {
                appRepository.lockApp(it.packageName, it.appName)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val APP_NAME = "app_name"
        const val APP_PACKAGE = "app_package"
        val worker =
            OneTimeWorkRequestBuilder<EmergencyUnlockWorker>().setInitialDelay(1, TimeUnit.DAYS)
                .build()
    }
} 