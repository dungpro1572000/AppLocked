package com.dungz.applocker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@get:Composable
val textTitleStyle get() = MaterialTheme.typography.headlineMedium

@get:Composable
val textSubTitleStyle get() = MaterialTheme.typography.bodyLarge

@get:Composable
val textNormalStyle get() = MaterialTheme.typography.bodyMedium.copy(
    color = MaterialTheme.colorScheme.onSurfaceVariant
)

@get:Composable
val textInButtonStyle get() = MaterialTheme.typography.labelMedium

@get:Composable
val textErrorStyle get() = MaterialTheme.typography.bodyMedium.copy(
    color = MaterialTheme.colorScheme.error
)