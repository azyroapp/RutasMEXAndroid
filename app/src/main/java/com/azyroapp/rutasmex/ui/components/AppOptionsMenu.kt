package com.azyroapp.rutasmex.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Menú de opciones de la app (3 puntos)
 * Replica el AppOptionsMenu.swift de iOS
 */
@Composable
fun AppOptionsMenu(
    mapType: com.azyroapp.rutasmex.ui.viewmodel.MapType,
    onToggleMapType: () -> Unit,
    onShowProximityConfig: () -> Unit,
    onShowSavedPlaces: () -> Unit,
    onShowFavorites: () -> Unit,
    onShowHistory: () -> Unit,
    onShowSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Sección 1: Mapa y Visualización
            DropdownMenuItem(
                text = {
                    Text(
                        if (mapType == com.azyroapp.rutasmex.ui.viewmodel.MapType.NORMAL) {
                            "Vista Satélite"
                        } else {
                            "Mapa Estándar"
                        }
                    )
                },
                onClick = {
                    onToggleMapType()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (mapType == com.azyroapp.rutasmex.ui.viewmodel.MapType.NORMAL) {
                            Icons.Default.Public
                        } else {
                            Icons.Default.Map
                        },
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenuItem(
                text = { Text("Configurar Radios") },
                onClick = {
                    onShowProximityConfig()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null
                    )
                }
            )
            
            HorizontalDivider()
            
            // Sección 2: Datos y Recursos del Usuario
            DropdownMenuItem(
                text = { Text("Mis Lugares") },
                onClick = {
                    onShowSavedPlaces()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenuItem(
                text = { Text("Favoritos") },
                onClick = {
                    onShowFavorites()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null
                    )
                }
            )
            
            DropdownMenuItem(
                text = { Text("Historial") },
                onClick = {
                    onShowHistory()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null
                    )
                }
            )
            
            HorizontalDivider()
            
            // Sección 3: Configuración
            DropdownMenuItem(
                text = { Text("Configuración") },
                onClick = {
                    onShowSettings()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            )
            
            HorizontalDivider()
            
            // Sección 4: Compartir
            DropdownMenuItem(
                text = { Text("Compartir App") },
                onClick = {
                    shareApp(context)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

/**
 * Comparte la app usando Intent de Android
 */
private fun shareApp(context: android.content.Context) {
    val appUrl = "https://play.google.com/store/apps/details?id=com.azyroapp.rutasmex"
    val message = "¡Descubre RutasMEX! 🚌 La mejor app para encontrar rutas de transporte público en Chiapas. 📍"
    
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$message\n\n$appUrl")
    }
    
    context.startActivity(
        Intent.createChooser(shareIntent, "Compartir RutasMEX")
    )
}
