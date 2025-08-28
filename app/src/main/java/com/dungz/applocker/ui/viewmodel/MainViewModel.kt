package com.dungz.applocker.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungz.applocker.data.model.AppInfo
import com.dungz.applocker.data.model.SecuritySettings
import com.dungz.applocker.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _lockedApps = MutableStateFlow<List<AppInfo>>(emptyList())

    init {
        loadInitialData()
    }

    suspend fun isPasswordSet(): Boolean {
        val settings = appRepository.getSecuritySettings()
        return settings.isPasswordSet
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load security settings
            val settings = appRepository.getSecuritySettings()
            Log.d("DungNT3544", "Security Settings: $settings")
            _uiState.value = _uiState.value.copy(
                securitySettings = settings,
                isPasswordSet = settings.isPasswordSet
            )

            // Load locked apps
            appRepository.getLockedApps().collect { lockedApps ->
                val allApps = appRepository.getAllInstalledApps()
                val updatedApps = allApps.map { app ->
                    app.copy(isLocked = lockedApps.any { it.packageName == app.packageName })
                }
                _lockedApps.value = updatedApps
            }
        }
    }
}

data class MainUiState(
    val securitySettings: SecuritySettings = SecuritySettings(),
    val isPasswordSet: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) 