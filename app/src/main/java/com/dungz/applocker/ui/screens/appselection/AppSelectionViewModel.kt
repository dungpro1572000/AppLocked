package com.dungz.applocker.ui.screens.appselection

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val permissionHelper: PermissionHelper,
    private val appAlarm: AppAlarm,
) :
    ViewModel() {
    private val _state = MutableStateFlow(AppSelectionState())
    val state: StateFlow<AppSelectionState> = _state

    // Cache apps data to avoid reloading on every Flow emit
    private var cachedApps: List<AppSelectionInfo> = emptyList()
    private var appsLoaded = false

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading state
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(isLoading = true)
            }

            // Load all installed apps ONCE and cache bitmap
            if (!appsLoaded) {
                val allApps = appRepository.getAllInstalledApps()
                cachedApps = allApps.map { app ->
                    AppSelectionInfo(
                        packageName = app.packageName,
                        appName = app.appName,
                        appIcon = app.appIcon.toBitmap().asImageBitmap(),
                        isSystemApp = app.isSystemApp,
                        isSelected = false,
                        isLocked = false
                    )
                }
                appsLoaded = true
            }

            // Observe locked apps changes and update only isLocked status
            appRepository.getLockedApps().collectLatest { lockedApps ->
                val lockedPackages = lockedApps.map { it.packageName }.toSet()

                val updatedApps = cachedApps.map { app ->
                    app.copy(isLocked = lockedPackages.contains(app.packageName))
                }

                // Filter on background thread before updating UI
                val currentState = _state.value
                val filtered = filterApps(
                    updatedApps,
                    currentState.searchApp,
                    currentState.isShowSystemApp
                )

                withContext(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        listApp = updatedApps,
                        listLockedApp = updatedApps,
                        filteredApps = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateSearchApp(search: String) {
        _state.value = _state.value.copy(searchApp = search)
        updateFilteredApps()
    }

    fun updateIsShowSystemApp(value: Boolean) {
        _state.value = _state.value.copy(isShowSystemApp = value)
        updateFilteredApps()
    }

    private fun filterApps(
        apps: List<AppSelectionInfo>,
        searchQuery: String,
        showSystemApps: Boolean
    ): List<AppSelectionInfo> {
        return apps.filter { app ->
            val matchesSearch =
                app.appName.contains(searchQuery, ignoreCase = true) ||
                        app.packageName.contains(searchQuery, ignoreCase = true)
            val matchesSystemFilter = showSystemApps || !app.isSystemApp
            matchesSearch && matchesSystemFilter
        }
    }

    private fun updateFilteredApps() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val filtered = filterApps(
                currentState.listLockedApp,
                currentState.searchApp,
                currentState.isShowSystemApp
            )
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(filteredApps = filtered)
            }
        }
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
        // Optimistic update - update UI immediately
        val newLockedState = !isLocked
        cachedApps = cachedApps.map { app ->
            if (app.packageName == packageName) {
                app.copy(isLocked = newLockedState)
            } else {
                app
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _state.value
            val updatedList = currentState.listLockedApp.map { app ->
                if (app.packageName == packageName) {
                    app.copy(isLocked = newLockedState)
                } else {
                    app
                }
            }
            val filtered = filterApps(
                updatedList,
                currentState.searchApp,
                currentState.isShowSystemApp
            )

            withContext(Dispatchers.Main) {
                _state.value = currentState.copy(
                    listLockedApp = updatedList,
                    filteredApps = filtered
                )
            }

            // Perform database operation in background
            withContext(Dispatchers.IO) {
                if (isLocked) {
                    appRepository.unlockApp(packageName)
                } else {
                    appRepository.lockApp(packageName, appName)
                }
            }
        }
    }

    fun isUsageStatsPermissionGrant(): Boolean =
        permissionHelper.hasUsageStatsPermission()

    fun isOverlayPermissionGrant(): Boolean {
        return permissionHelper.hasOverlayPermission()
    }

    fun toggleAppUnlockTimer(packageName: String, appName: String, durationMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (durationMinutes > 0) {
                appAlarm.setAlarmForOpenLockedApps(appName, packageName, durationMinutes)
            }
        }
    }
}