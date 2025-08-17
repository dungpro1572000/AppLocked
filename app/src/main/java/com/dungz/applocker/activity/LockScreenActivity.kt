package com.dungz.applocker.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dungz.applocker.ui.screens.passwordprompt.PasswordPromptScreen
import com.dungz.applocker.ui.theme.AppLockerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppLockerTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    PasswordPromptScreen(onSuccess = {

                    })
                }
            }
        }
    }
}