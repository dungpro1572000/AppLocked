package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.dungz.applocker.ui.theme.AppLockerTextColors
import com.dungz.applocker.ui.theme.AppLockerTextStyles

@Composable
fun <T, R> BaseConfirmDialog(
    title: String,
    content: @Composable () -> Unit,
    initValue: T,
    onDismiss: () -> Unit,
    onConfirm: (T) -> R,
    confirmButtonContent: String = "Ok",
    dismissButtonContent: String = "Cancel"
) {
    val data = rememberSaveable { mutableStateOf(initValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true,
        ),
        title = {
            Text(
                title, style = AppLockerTextStyles.DialogTitle,
                color = AppLockerTextColors.DialogTitleTextDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = content,
        confirmButton = {
            Button(
                onClick = { onConfirm.invoke(data.value) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    confirmButtonContent,
                    style = AppLockerTextStyles.ButtonPrimary,
                    color = AppLockerTextColors.ButtonPrimaryTextDark
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    dismissButtonContent,
                    style = AppLockerTextStyles.ButtonSecondary,
                    color = AppLockerTextColors.ButtonSecondaryTextDark
                )
            }
        }
    )
}