package com.dungz.applocker.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val LightPrimary = Color(0xFF1976D2)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFBBDEFB)
val LightOnPrimaryContainer = Color(0xFF0D47A1)
val LightSecondary = Color(0xFF424242)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE0E0E0)
val LightOnSecondaryContainer = Color(0xFF212121)
val LightTertiary = Color(0xFF4CAF50)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFC8E6C9)
val LightOnTertiaryContainer = Color(0xFF2E7D32)
val LightError = Color(0xFFD32F2F)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFCDD2)
val LightOnErrorContainer = Color(0xFFB71C1C)
val LightBackground = Color(0xFFFAFAFA)
val LightOnBackground = Color(0xFF212121)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF212121)
val LightSurfaceVariant = Color(0xFFF5F5F5)
val LightOnSurfaceVariant = Color(0xFF424242)
val LightOutline = Color(0xFFBDBDBD)
val LightOutlineVariant = Color(0xFFE0E0E0)
val LightInverseOnSurface = Color(0xFFF5F5F5)
val LightInverseSurface = Color(0xFF212121)
val LightInversePrimary = Color(0xFF90CAF9)
val LightShadow = Color(0xFF000000)
val LightSurfaceTint = Color(0xFF1976D2)
val LightOutlineVariant2 = Color(0xFFC7C7C7)

// Dark Theme Colors
val DarkPrimary = Color(0xFF90CAF9)
val DarkOnPrimary = Color(0xFF0D47A1)
val DarkPrimaryContainer = Color(0xFF1565C0)
val DarkOnPrimaryContainer = Color(0xFFE3F2FD)
val DarkSecondary = Color(0xFFBDBDBD)
val DarkOnSecondary = Color(0xFF212121)
val DarkSecondaryContainer = Color(0xFF424242)
val DarkOnSecondaryContainer = Color(0xFFE0E0E0)
val DarkTertiary = Color(0xFF81C784)
val DarkOnTertiary = Color(0xFF2E7D32)
val DarkTertiaryContainer = Color(0xFF388E3C)
val DarkOnTertiaryContainer = Color(0xFFC8E6C9)
val DarkError = Color(0xFFEF5350)
val DarkOnError = Color(0xFFB71C1C)
val DarkErrorContainer = Color(0xFFC62828)
val DarkOnErrorContainer = Color(0xFFFFCDD2)
val DarkBackground = Color(0xFF121212)
val DarkOnBackground = Color(0xFFE0E0E0)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color(0xFFE0E0E0)
val DarkSurfaceVariant = Color(0xFF2D2D2D)
val DarkOnSurfaceVariant = Color(0xFFBDBDBD)
val DarkOutline = Color(0xFF424242)
val DarkOutlineVariant = Color(0xFF2D2D2D)
val DarkInverseOnSurface = Color(0xFF1E1E1E)
val DarkInverseSurface = Color(0xFFE0E0E0)
val DarkInversePrimary = Color(0xFF1976D2)
val DarkShadow = Color(0xFF000000)
val DarkSurfaceTint = Color(0xFF90CAF9)
val DarkOutlineVariant2 = Color(0xFF424242)

// Custom Colors
val SuccessGreen = Color(0xFF4CAF50)
val WarningOrange = Color(0xFFFF9800)
val DangerRed = Color(0xFFF44336)
val InfoBlue = Color(0xFF2196F3)
val LockedAppColor = Color(0xFFFF5722)
val UnlockedAppColor = Color(0xFF4CAF50)
val EmergencyPasswordColor = Color(0xFFFF9800)

// Text Colors for Light/Dark Theme Support
object AppLockerTextColors {
    // Primary text colors - main content
    val PrimaryTextLight = Color(0xFF212121)      // Dark text on light background
    val PrimaryTextDark = Color(0xFFE0E0E0)       // Light text on dark background
    
    // Secondary text colors - less important content
    val SecondaryTextLight = Color(0xFF424242)    // Medium gray on light background
    val SecondaryTextDark = Color(0xFFBDBDBD)     // Light gray on dark background
    
    // Tertiary text colors - disabled or subtle content
    val TertiaryTextLight = Color(0xFF757575)     // Light gray on light background
    val TertiaryTextDark = Color(0xFF9E9E9E)      // Medium gray on dark background
    
    // Disabled text colors
    val DisabledTextLight = Color(0xFFBDBDBD)     // Very light gray on light background
    val DisabledTextDark = Color(0xFF616161)      // Dark gray on dark background
    
    // Success text colors
    val SuccessTextLight = Color(0xFF2E7D32)      // Dark green on light background
    val SuccessTextDark = Color(0xFF81C784)       // Light green on dark background
    
    // Warning text colors
    val WarningTextLight = Color(0xFFE65100)      // Dark orange on light background
    val WarningTextDark = Color(0xFFFFB74D)       // Light orange on dark background
    
    // Error text colors
    val ErrorTextLight = Color(0xFFB71C1C)        // Dark red on light background
    val ErrorTextDark = Color(0xFFEF5350)         // Light red on dark background
    
    // Info text colors
    val InfoTextLight = Color(0xFF1565C0)         // Dark blue on light background
    val InfoTextDark = Color(0xFF64B5F6)          // Light blue on dark background
    
    // Link text colors
    val LinkTextLight = Color(0xFF1976D2)         // Blue link on light background
    val LinkTextDark = Color(0xFF90CAF9)          // Light blue link on dark background
    
    // Caption and small text colors
    val CaptionTextLight = Color(0xFF9E9E9E)      // Light gray for captions on light background
    val CaptionTextDark = Color(0xFF757575)       // Medium gray for captions on dark background
    
    // Placeholder text colors
    val PlaceholderTextLight = Color(0xFFBDBDBD)  // Light gray for placeholders on light background
    val PlaceholderTextDark = Color(0xFF616161)   // Dark gray for placeholders on dark background
    
    // Security-specific text colors
    val SecurityWarningTextLight = Color(0xFFE65100)  // Orange for security warnings on light background
    val SecurityWarningTextDark = Color(0xFFFFB74D)   // Light orange for security warnings on dark background
    
    val SecurityErrorTextLight = Color(0xFFD32F2F)    // Red for security errors on light background
    val SecurityErrorTextDark = Color(0xFFEF5350)     // Light red for security errors on dark background
    
    val SecuritySuccessTextLight = Color(0xFF2E7D32)  // Green for security success on light background
    val SecuritySuccessTextDark = Color(0xFF81C784)   // Light green for security success on dark background
    
    // App status text colors
    val LockedAppTextLight = Color(0xFFD84315)        // Dark orange for locked apps on light background
    val LockedAppTextDark = Color(0xFFFF7043)         // Light orange for locked apps on dark background
    
    val UnlockedAppTextLight = Color(0xFF388E3C)      // Dark green for unlocked apps on light background
    val UnlockedAppTextDark = Color(0xFF66BB6A)       // Light green for unlocked apps on dark background
    
    // Password/PIN display colors
    val PasswordTextLight = Color(0xFF424242)         // Medium gray for password text on light background
    val PasswordTextDark = Color(0xFFBDBDBD)          // Light gray for password text on dark background
    
    val PinDisplayTextLight = Color(0xFF1976D2)       // Blue for PIN display on light background
    val PinDisplayTextDark = Color(0xFF90CAF9)        // Light blue for PIN display on dark background
    
    // Button text colors
    val ButtonPrimaryTextLight = Color(0xFFFFFFFF)    // White text on primary buttons (light theme)
    val ButtonPrimaryTextDark = Color(0xFF0D47A1)     // Dark text on primary buttons (dark theme)
    
    val ButtonSecondaryTextLight = Color(0xFF1976D2)  // Blue text on secondary buttons (light theme)
    val ButtonSecondaryTextDark = Color(0xFF90CAF9)   // Light blue text on secondary buttons (dark theme)
    
    // Dialog text colors
    val DialogTitleTextLight = Color(0xFF212121)      // Dark text for dialog titles on light background
    val DialogTitleTextDark = Color(0xFFE0E0E0)       // Light text for dialog titles on dark background
    
    val DialogMessageTextLight = Color(0xFF424242)    // Medium gray for dialog messages on light background
    val DialogMessageTextDark = Color(0xFFBDBDBD)     // Light gray for dialog messages on dark background
    
    // Settings text colors
    val SettingTitleTextLight = Color(0xFF212121)     // Dark text for setting titles on light background
    val SettingTitleTextDark = Color(0xFFE0E0E0)      // Light text for setting titles on dark background
    
    val SettingDescriptionTextLight = Color(0xFF424242) // Medium gray for setting descriptions on light background
    val SettingDescriptionTextDark = Color(0xFFBDBDBD)  // Light gray for setting descriptions on dark background
    
    // Emergency and special text colors
    val EmergencyTextLight = Color(0xFFE65100)        // Orange for emergency text on light background
    val EmergencyTextDark = Color(0xFFFFB74D)         // Light orange for emergency text on dark background
    
    // Overline and category text colors
    val OverlineTextLight = Color(0xFF757575)         // Medium gray for overlines on light background
    val OverlineTextDark = Color(0xFF9E9E9E)          // Light gray for overlines on dark background
}