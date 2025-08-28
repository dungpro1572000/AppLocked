package com.dungz.applocker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dungz.applocker.ui.screens.SecurityCheckScreen
import com.dungz.applocker.ui.screens.appselection.AppSelectionScreen
import com.dungz.applocker.ui.screens.emergencypasswordsetup.EmergencyPasswordSetupScreen
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptScreen
import com.dungz.applocker.ui.screens.passwordsetup.PasswordSetupScreen
import com.dungz.applocker.ui.screens.settings.SettingsScreen
import com.dungz.applocker.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    globalViewModel: MainViewModel = hiltViewModel()
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination =
            if (globalViewModel.isPasswordSet()) Screen.PasswordPrompt.route else Screen.PasswordSetup.route
    }
    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            composable(Screen.SecurityCheck.route) {
                SecurityCheckScreen(navController)
            }

            composable(Screen.PasswordSetup.route) {
                PasswordSetupScreen(navController)
            }

            composable(Screen.AppSelection.route) {
                AppSelectionScreen(navController)
            }

            composable(Screen.PasswordPrompt.route) {
                PasswordPromptScreen(onSuccess = {
                    navController.navigate(Screen.AppSelection.route) {
                        popUpTo(Screen.PasswordPrompt.route) { inclusive = true }
                    }
                }, onEmergencyUnLock = {
                    navController.navigate(Screen.EmergencyPasswordSetup.route)
                })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }

            composable(Screen.EmergencyPasswordSetup.route) {
                EmergencyPasswordSetupScreen(navController)
            }
        }
    }

} 