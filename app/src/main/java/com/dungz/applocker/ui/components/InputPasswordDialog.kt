package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun InputPasswordDialog(
    title: String,
    onDismiss: () -> Unit, onConfirm: (String) -> Unit
) {

    val text = rememberSaveable {
        mutableStateOf("")
    }
    val isShowPassword = rememberSaveable {
        mutableStateOf(false)
    }
    BaseConfirmDialog(
        title = title,
        initValue = text.value,
        content = {
            Column {
                OutlinedTextField(
                    value = text.value,
                    onValueChange = {
                        text.value = it
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),visualTransformation = if (isShowPassword.value) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            isShowPassword.value = !isShowPassword.value
                        }) {
                            Icon(
                                imageVector = if (isShowPassword.value) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (isShowPassword.value) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        onConfirm = { onConfirm(text.value) },
        confirmButtonContent = "Unlock",
        onDismiss = onDismiss,
    )
}