package com.dungz.applocker.data.datastore

import android.content.Context
import android.provider.Telephony.Carriers.PASSWORD
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dungz.applocker.data.model.SecuritySettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class AppDataStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    suspend fun getSecuritySettings(): SecuritySettings {
        val preferences = context.dataStore.data.first()
        return SecuritySettings(
            isPasswordSet = preferences[booleanPreferencesKey(IS_PASSWORD_SET)] ?: false,
            password = preferences[stringPreferencesKey(PASSWORD)] ?: "",
            emergencyPassword = preferences[stringPreferencesKey(EMERGENCY_PASSWORD)] ?: "",
            isEmergencyPasswordSet = preferences[booleanPreferencesKey(IS_EMERGENCY_PASSWORD_SET)]
                ?: false,
            emergencyUnlockUntil = preferences[longPreferencesKey(EMERGENCY_UNLOCK_UNTIL)] ?: 0L,
            failedAttempts = preferences[intPreferencesKey(FAILED_ATTEMPTS)] ?: 0,
            lastFailedAttempt = preferences[longPreferencesKey(LAST_FAILED_ATTEMPT)] ?: 0L,
            isBiometricEnabled = preferences[booleanPreferencesKey(IS_BIOMETRIC_ENABLED)] ?: false
        )
    }

    suspend fun saveSecuritySettings(settings: SecuritySettings) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(IS_PASSWORD_SET)] = settings.isPasswordSet
            preferences[stringPreferencesKey(PASSWORD)] = settings.password
            preferences[stringPreferencesKey(EMERGENCY_PASSWORD)] = settings.emergencyPassword
            preferences[booleanPreferencesKey(IS_EMERGENCY_PASSWORD_SET)] =
                settings.isEmergencyPasswordSet
            preferences[longPreferencesKey(EMERGENCY_UNLOCK_UNTIL)] = settings.emergencyUnlockUntil
            preferences[intPreferencesKey(FAILED_ATTEMPTS)] = settings.failedAttempts
            preferences[longPreferencesKey(LAST_FAILED_ATTEMPT)] = settings.lastFailedAttempt
            preferences[booleanPreferencesKey(IS_BIOMETRIC_ENABLED)] = settings.isBiometricEnabled
        }
    }

    suspend fun deleteDataStore() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "security_settings"
        private const val IS_PASSWORD_SET = "is_password_set"
        private const val PASSWORD = "password"
        private const val EMERGENCY_PASSWORD = "emergency_password"
        private const val IS_EMERGENCY_PASSWORD_SET = "is_emergency_password_set"
        private const val EMERGENCY_UNLOCK_UNTIL = "emergency_unlock_until"
        private const val FAILED_ATTEMPTS = "failed_attempts"
        private const val LAST_FAILED_ATTEMPT = "last_failed_attempt"
        private const val IS_BIOMETRIC_ENABLED = "is_biometric_enabled"
    }
}