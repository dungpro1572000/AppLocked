package com.dungz.applocker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dungz.applocker.ui.navigation.Screen
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.viewmodel.MainViewModel

@Composable
fun EmergencyPasswordSetupScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var emergencyPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Emergency Password",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimen.spacingLarge))
        
        Text(
            text = "Set an emergency password that will unlock all apps for 24 hours when used",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(Dimen.spacingExtraLarge))
        
        OutlinedTextField(
            value = emergencyPassword,
            onValueChange = { 
                emergencyPassword = it
                error = null
            },
            label = { Text("Emergency Password") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (showPassword) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(Dimen.spacingLarge))
        
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                error = null
            },
            label = { Text("Confirm Emergency Password") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (showConfirmPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                    Icon(
                        imageVector = if (showConfirmPassword) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        error?.let { errorMessage ->
            Spacer(modifier = Modifier.height(Dimen.spacingMedium))
            
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(Dimen.spacingExtraLarge))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimen.spacingMedium)
        ) {
            Button(
                onClick = {
                    when {
                        emergencyPassword.length < 4 -> {
                            error = "Password must be at least 4 characters"
                        }
                        emergencyPassword != confirmPassword -> {
                            error = "Passwords do not match"
                        }
                        else -> {
                            viewModel.setEmergencyPassword(emergencyPassword)
                            navController.navigate(Screen.AppSelection.route) {
                                popUpTo(Screen.EmergencyPasswordSetup.route) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = emergencyPassword.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                Text("Set Emergency Password")
            }
            
            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.AppSelection.route) {
                        popUpTo(Screen.EmergencyPasswordSetup.route) { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip")
            }
        }
    }
} 