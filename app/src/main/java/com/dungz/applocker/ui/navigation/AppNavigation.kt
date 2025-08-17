package com.dungz.applocker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dungz.applocker.ui.screens.*
import com.dungz.applocker.ui.screens.appselection.AppSelectionScreen
import com.dungz.applocker.ui.screens.emergencypasswordsetup.EmergencyPasswordSetupScreen
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptScreen
import com.dungz.applocker.ui.screens.passwordsetup.PasswordSetupScreen
import com.dungz.applocker.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.SecurityCheck.route
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