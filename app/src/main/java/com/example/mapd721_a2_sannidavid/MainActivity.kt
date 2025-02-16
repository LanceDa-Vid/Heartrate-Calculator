package com.example.mapd721_a2_sannidavid



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.PermissionController
import com.example.mapd721_a2_sannidavid.HealthConnectManager
import com.example.mapd721_a2_sannidavid.MainScreen
import com.example.mapd721_a2_sannidavid.ui.theme.MAPD721A2SanniDavidTheme
import com.example.mapd721_a2_sannidavid.HeartRateViewModel


import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    private lateinit var healthConnectManager: HealthConnectManager
    private var showMainContent by mutableStateOf(false)
    private val viewModel: HeartRateViewModel by viewModels {
        HeartRateViewModel.Factory(healthConnectManager)
    }
    @Composable
    fun PermissionScreen(onRequestPermissions: () -> Unit) {
        Button(onClick = onRequestPermissions) {
            Text("Grant Permissions")
        }
    }
    private val permissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { permissions ->
        if (permissions.containsAll(HealthConnectManager.PERMISSIONS)) {
            showMainContent = true
        } else {
            Toast.makeText(
                this,
                "Health Connect permissions are required",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectManager = HealthConnectManager(this)

        setContent {
            MAPD721A2SanniDavidTheme{
                if (showMainContent) {
                    MainScreen(viewModel = viewModel)
                } else {
                    PermissionScreen(
                        onRequestPermissions = {
                            permissionLauncher.launch(HealthConnectManager.PERMISSIONS)
                        }
                    )
                }
            }
        }
    }
}