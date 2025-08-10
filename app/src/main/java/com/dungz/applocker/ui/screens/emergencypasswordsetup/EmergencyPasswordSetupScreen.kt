package com.dungz.applocker.ui.screens.emergencypasswordsetup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
fun EmergencyPasswordSetupScreen(
    navController: NavController,
    viewModel: EmergencyPasswordViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

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
            value = state.value.password,
            onValueChange = {
                viewModel.updatePassword(it)
                viewModel.updateError(null)
            },
            label = { Text("Emergency Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (state.value.showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = {
                    val showPassword = !state.value.showPassword
                    viewModel.updateShowPassword(showPassword)
                }) {
                    Icon(
                        imageVector = if (state.value.showPassword) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (state.value.showPassword) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimen.spacingLarge))

        OutlinedTextField(
            value = state.value.confirmPassword,
            onValueChange = {
                viewModel.updateConfirmPassword(it)
                viewModel.updateError(null)
            },
            label = { Text("Confirm Emergency Password") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (state.value.showConfirmPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = {
                    val showConfirmPassword = !state.value.showConfirmPassword
                    viewModel.updateShowConfirmPassword(showConfirmPassword)
                }) {
                    Icon(
                        imageVector = if (state.value.showConfirmPassword) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (state.value.showConfirmPassword) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        state.value.error?.let { errorMessage ->
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
                        state.value.password.length < 4 -> {
                            viewModel.updateError("Password must be at least 4 characters")
                        }

                        state.value.password != state.value.confirmPassword -> {
                            viewModel.updateError("Passwords do not match")
                        }

                        else -> {
                            viewModel.updateEmergencyPassword()
                            navController.navigate(Screen.AppSelection.route) {
                                popUpTo(Screen.EmergencyPasswordSetup.route) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = state.value.password.isNotEmpty() && state.value.confirmPassword.isNotEmpty()
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