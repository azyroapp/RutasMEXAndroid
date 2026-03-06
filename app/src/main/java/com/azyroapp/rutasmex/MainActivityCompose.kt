package com.azyroapp.rutasmex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.azyroapp.rutasmex.core.services.OnboardingService
import com.azyroapp.rutasmex.ui.navigation.AppNavigation
import com.azyroapp.rutasmex.ui.screens.OnboardingScreen
import com.azyroapp.rutasmex.ui.theme.RutasMEXTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * MainActivity con Jetpack Compose
 * Muestra Onboarding en primera apertura, luego la app principal
 */
@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    
    @Inject
    lateinit var onboardingService: OnboardingService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            RutasMEXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estado para controlar si mostrar onboarding
                    var showOnboarding by remember { 
                        mutableStateOf(!onboardingService.hasSeenOnboarding) 
                    }
                    
                    if (showOnboarding) {
                        // Primera vez: mostrar onboarding
                        OnboardingScreen(
                            onboardingService = onboardingService,
                            onComplete = {
                                showOnboarding = false
                            }
                        )
                    } else {
                        // Ya vio onboarding: mostrar app principal
                        AppNavigation()
                    }
                }
            }
        }
    }
}
