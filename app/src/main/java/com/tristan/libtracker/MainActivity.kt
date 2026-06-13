package com.tristan.libtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.ui.tooling.preview.Preview
import com.tristan.libtracker.ui.screen.MainScreen
import com.tristan.libtracker.ui.theme.LibTrackerTheme
import com.tristan.libtracker.ui.viewmodel.StationViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = (application as LibTrackerApplication)
        val viewModel: StationViewModel by viewModels { 
            StationViewModel.Factory(app.stationRepository, app.locationClient) 
        }

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                viewModel.updateLocation()
            }
        }
        
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        
        enableEdgeToEdge()
        setContent {
            LibTrackerTheme {
                MainScreen(viewModel)
            }
        }
    }
}
