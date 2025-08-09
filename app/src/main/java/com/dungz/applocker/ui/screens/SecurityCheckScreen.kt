package com.dungz.applocker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        if (viewModel.checkDeviceSecurity()) {
            // In a real app, you would trigger biometric authentication here
            // For now, we'll proceed directly
            if (!uiState.isPasswordSet) {
                navController.navigate(Screen.PasswordSetup.route) {
                    popUpTo(Screen.SecurityCheck.route) { inclusive = true }
                }
            } else {
                navController.navigate(Screen.AppSelection.route) {
                    popUpTo(Screen.SecurityCheck.route) { inclusive = true }
                }
            }
        } else {
            // No device security, proceed to password setup
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