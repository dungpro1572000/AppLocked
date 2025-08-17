package com.dungz.applocker.ui.screens.passwordprompt

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.theme.LocalAppColorScheme

@Composable
fun PasswordPromptScreen(
    onSuccess: () -> Unit = {},
    onEmergencyUnLock: () -> Unit = {},
    viewModel: PasswordPromptViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColorScheme.current.background)
            .padding(Dimen.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "App Locked",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (state.value.isShowNormalPasswordView) {
            Spacer(modifier = Modifier.height(Dimen.spacingLarge))

            Text(
                text = "Enter your password to unlock this app",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimen.spacingExtraLarge))

            OutlinedTextField(
                value = state.value.password,
                onValueChange = {
                    viewModel.updatePassword(it)
                },
                label = { Text("Password") },
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
                        viewModel.toggleShowPassword()
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
                        viewModel.validatePassword(
                            password = state.value.password,
                            onSuccess = {
                                onSuccess.invoke()
                            },
                            onError = {
                                viewModel.updateError("Incorrect password")
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.value.password.isNotEmpty()
                ) {
                    Text("Unlock")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.toggleNormalPasswordView()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Emergency")
                }
            }
        }
        else {
            Spacer(modifier = Modifier.height(Dimen.spacingLarge))

            Text(
                text = "Emergency Password",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimen.spacingMedium))

            OutlinedTextField(
                value = state.value.password,
                onValueChange = {
                    viewModel.updatePassword(it)
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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimen.spacingMedium))

            Button(
                onClick = {
                    viewModel.validatePassword(
                        password = state.value.password,
                        onSuccess = {
                            onEmergencyUnLock.invoke()
                        },
                        onError = {
                            viewModel.updateError("Incorrect emergency password")
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.value.password.isNotEmpty()
            ) {
                Text("Emergency Unlock (24h)")
            }
        }

        Spacer(modifier = Modifier.height(Dimen.spacingLarge))

        TextButton(
            onClick = { viewModel.toggleNormalPasswordView() }
        ) {
            Text("Cancel")
        }
    }
} 