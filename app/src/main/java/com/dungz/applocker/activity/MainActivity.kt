package com.dungz.applocker.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dungz.applocker.service.AppLockService
import com.dungz.applocker.ui.navigation.AppNavigation
import com.dungz.applocker.ui.theme.AppLockerTheme
import com.dungz.applocker.util.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var permissionHelper: PermissionHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppLockerTheme {
                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
        if (permissionHelper.hasUsageStatsPermission()) {
            startService(Intent(this, AppLockService::class.java))
        } else {
            permissionHelper.requestUsageStatsPermission(activity = this) { granted ->
                if (granted) {
                    startService(Intent(this, AppLockService::class.java))
                } else {
                    Log.d("DungNT354"," permisstion deny")
                }
            }
        }
    }
}