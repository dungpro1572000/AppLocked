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
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.viewmodel.MainViewModel

@Composable
fun PasswordPromptScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showEmergencyPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "App Locked",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimen.spacingLarge))
        
        Text(
            text = "Enter your password to unlock this app",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(Dimen.spacingExtraLarge))
        
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                error = null
            },
            label = { Text("Password") },
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
                    viewModel.validatePassword(
                        password = password,
                        onSuccess = {
                            // Grant access to the app
                            navController.popBackStack()
                        },
                        onError = {
                            error = "Incorrect password"
                            if (viewModel.shouldTakePhoto()) {
                                // Take photo logic would be handled here
                                error = "Too many failed attempts. Photo captured."
                            }
                        }
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = password.isNotEmpty()
            ) {
                Text("Unlock")
            }
            
            OutlinedButton(
                onClick = { showEmergencyPassword = !showEmergencyPassword },
                modifier = Modifier.weight(1f)
            ) {
                Text("Emergency")
            }
        }
        
        if (showEmergencyPassword) {
            Spacer(modifier = Modifier.height(Dimen.spacingLarge))
            
            Text(
                text = "Emergency Password",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Dimen.spacingMedium))
            
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
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
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Dimen.spacingMedium))
            
            Button(
                onClick = {
                    viewModel.validateEmergencyPassword(
                        password = password,
                        onSuccess = {
                            // Grant emergency access for 24 hours
                            navController.popBackStack()
                        },
                        onError = {
                            error = "Incorrect emergency password"
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = password.isNotEmpty()
            ) {
                Text("Emergency Unlock (24h)")
            }
        }
        
        Spacer(modifier = Modifier.height(Dimen.spacingLarge))
        
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Cancel")
        }
    }
} 