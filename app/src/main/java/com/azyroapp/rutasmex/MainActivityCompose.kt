package com.azyroapp.rutasmex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.azyroapp.rutasmex.ui.navigation.AppNavigation
import com.azyroapp.rutasmex.ui.screens.HomeScreen
import com.azyroapp.rutasmex.ui.theme.RutasMEXTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity con Jetpack Compose
 */
@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            RutasMEXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
