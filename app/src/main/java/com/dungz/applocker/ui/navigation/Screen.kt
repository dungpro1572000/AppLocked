package com.dungz.applocker.ui.navigation

sealed class Screen(val route: String) {

    object SecurityCheck : Screen("security_check")

    object PasswordSetup : Screen("password_setup")

    object AppSelection : Screen("app_selection")

    object PasswordPrompt : Screen("password_prompt")

    object Settings : Screen("settings")

    object EmergencyPasswordSetup : Screen("emergency_password_setup")

    // You can add arguments like this if needed:
    // object PasswordPrompt : Screen("password_prompt/{packageName}") {
    //     fun createRoute(packageName: String) = "password_prompt/$packageName"
    // }
}
