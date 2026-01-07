package com.dungz.applocker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.dungz.applocker.ui.theme.textTitleStyle

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
    val data = remember { mutableStateOf(initValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
        title = {
            Text(
                title, style = textTitleStyle,
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
                )
            }
        }
    )
}