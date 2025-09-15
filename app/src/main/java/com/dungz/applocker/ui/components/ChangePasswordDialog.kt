package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dungz.applocker.ui.theme.Dimen
import com.dungz.applocker.ui.theme.textErrorStyle

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    BaseConfirmDialog(
        title = "Change Password",
        initValue = newPassword,
        content = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        error = null
                    },
                    label = { Text("New Password") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(Dimen.spacingMedium))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = { Text("Confirm Password") },
                    singleLine = true
                )

                error?.let { errorMessage ->
                    Spacer(modifier = Modifier.height(Dimen.spacingMedium))
                    Text(
                        text = errorMessage,
                        style = textErrorStyle
                    )
                }
            }
        },
        onDismiss = onDismiss,
        onConfirm = { onConfirm.invoke(newPassword) }
    )
}