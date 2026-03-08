package com.fitpulse.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitpulse.pro.navigation.FitPulseAppScaffold
import com.fitpulse.pro.ui.theme.FitPulseProTheme
import com.fitpulse.pro.viewmodel.FitPulseViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            FitPulseProTheme {
                val viewModel: FitPulseViewModel = viewModel()
                FitPulseAppScaffold(viewModel = viewModel)
            }
        }
    }
}
