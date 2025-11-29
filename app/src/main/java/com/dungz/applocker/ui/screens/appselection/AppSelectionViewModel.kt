package com.dungz.applocker.ui.screens.appselection

import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.alarm.AppAlarm
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val permissionHelper: PermissionHelper,
    private val appAlarm: AppAlarm,
) :
    ViewModel() {
    private val _state = MutableStateFlow(AppSelectionState())
    val state: StateFlow<AppSelectionState> = _state

    init {
        viewModelScope.launch(Dispatchers.IO){
            // Load locked apps
            appRepository.getLockedApps().collect { lockedApps ->
                val allApps = appRepository.getAllInstalledApps()
                val updatedApps = allApps.map { app ->
                    AppSelectionInfo(
                        packageName = app.packageName,
                        appName = app.appName,
                        appIcon = app.appIcon.toBitmap().asImageBitmap(),
                        isSystemApp = app.isSystemApp,
                        isSelected = false, // Default to false, can be updated later
                        isLocked = lockedApps.any { it.packageName == app.packageName }
                    )
                }
                _state.value = _state.value.copy(
                    listApp = updatedApps,
                    listLockedApp = updatedApps
                )
            }
        }
    }

    fun updateSearchApp(search: String) {
        _state.value = _state.value.copy(searchApp = search)
    }

    fun updateIsShowSystemApp(value: Boolean) {
        _state.value = _state.value.copy(isShowSystemApp = value)
    }

    fun updateIsShowSystemWindowAlertPermissionDialog(value: Boolean) {
        _state.value = _state.value.copy(
            isShowSystemWindowAlertPermissionDialog = value
        )
    }

    fun updateIsShowUsageStatsPermissionDialog(value: Boolean) {
        _state.value = _state.value.copy(
            isShowUsageStatsPermissionDialog = value
        )
    }

    fun updateIsShowDenyPermissionDialog(value: Boolean) {
        _state.value = _state.value.copy(
            isShowDenyPermissionDialog = value
        )
    }

    fun toggleAppLock(isLocked: Boolean, packageName: String, appName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isLocked) {
                appRepository.unlockApp(packageName)
            } else {
                appRepository.lockApp(packageName, appName)
            }
        }
    }

    fun isUsageStatsPermissionGrant(): Boolean =
        permissionHelper.hasUsageStatsPermission()

    fun isOverlayPermissionGrant(): Boolean {
        return permissionHelper.hasOverlayPermission()
    }

    fun toggleAppUnlockTimer(packageName: String, appName: String){
       viewModelScope.launch(Dispatchers.IO) {
           Log.d("DungNT354", "toggleAppUnlockTimer: $appName, $packageName")
           appAlarm.setAlarmForOpenLockedApps(appName,packageName, 5)
       }
    }
}