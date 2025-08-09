package com.dungz.applocker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dungz.applocker.data.repository.AppRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmergencyUnlockWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appRepository: AppRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Reset emergency unlock period
            val settings = appRepository.getSecuritySettings()
            val newSettings = settings.copy(
                emergencyUnlockUntil = 0L
            )
            appRepository.saveSecuritySettings(newSettings)
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
} 