package com.dungz.applocker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.viewmodel.MainViewModel
import com.dungz.applocker.util.BiometricHelper

@Composable
fun SecurityCheckScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val biometricHelper = remember { BiometricHelper(context) }

    var biometricError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (!viewModel.isPasswordSet()) {
            navController.navigate(Screen.PasswordSetup.route) {
                popUpTo(Screen.SecurityCheck.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.PasswordPrompt.route) {
                popUpTo(Screen.SecurityCheck.route) { inclusive = true }
            }
        }
//        if (viewModel.checkDeviceSecurity()) {
//            // In a real app, you would trigger biometric authentication here
//            // For now, we'll proceed directly
//
//        } else {
//            // No device security, proceed to password setup
//            if (!uiState.isPasswordSet) {
//                navController.navigate(Screen.PasswordSetup.route) {
//                    popUpTo(Screen.SecurityCheck.route) { inclusive = true }
//                }
//            } else {
//                navController.navigate(Screen.AppSelection.route) {
//                    popUpTo(Screen.SecurityCheck.route) { inclusive = true }
//                }
//            }
//        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(Dimen.iconSizeExtraLarge)
        )

        Spacer(modifier = Modifier.height(Dimen.spacingLarge))

        Text(
            text = "Security Check",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimen.spacingMedium))

        Text(
            text = "Verifying device security...",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        biometricError?.let { error ->
            Spacer(modifier = Modifier.height(Dimen.spacingLarge))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimen.spacingLarge))

            Button(
                onClick = {
                    if (!uiState.isPasswordSet) {
                        navController.navigate(Screen.PasswordSetup.route) {
                            popUpTo(Screen.SecurityCheck.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.AppSelection.route) {
                            popUpTo(Screen.SecurityCheck.route) { inclusive = true }
                        }
                    }
                }
            ) {
                Text("Continue without biometric")
            }
        }
    }
} 