package com.dungz.applocker.ui.screens.appselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.data.repository.AppRepository
import com.dungz.applocker.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val permissionHelper: PermissionHelper
) :
    ViewModel() {
    private val _state = MutableStateFlow(AppSelectionState())
    val state: StateFlow<AppSelectionState> = _state

    init {
        viewModelScope.launch {
            // Load locked apps
            appRepository.getLockedApps().collect { lockedApps ->
                val allApps = appRepository.getAllInstalledApps()
                val updatedApps = allApps.map { app ->
                    app.copy(isLocked = lockedApps.any { it.packageName == app.packageName })
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

    fun updateLockedListApp(list: List<AppInfo>) {
        _state.value = _state.value.copy(listLockedApp = list)
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

    fun toggleAppLock(appInfo: AppInfo) {
        viewModelScope.launch {
            if (appInfo.isLocked) {
                appRepository.unlockApp(appInfo.packageName)
            } else {
                appRepository.lockApp(appInfo)
            }
        }
    }

    fun isUsageStatsPermissionGrant(): Boolean =
        permissionHelper.hasUsageStatsPermission()

    fun isOverlayPermissionGrant(): Boolean {
        return permissionHelper.hasOverlayPermission()
    }
}