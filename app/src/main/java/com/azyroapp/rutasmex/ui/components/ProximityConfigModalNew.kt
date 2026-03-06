package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Modal de configuración de proximidad (3 radios: Far, Medium, Near)
 * Réplica del ProximityConfigModal.swift de iOS
 * 
 * DIFERENTE de RadiusConfigModal (que es para origen/destino)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProximityConfigModalNew(
    initialFarRadius: Double,
    initialMediumRadius: Double,
    initialNearRadius: Double,
    initialNotificationsEnabled: Boolean,
    initialSoundEnabled: Boolean,
    initialVibrationEnabled: Boolean,
    onConfigChanged: (Double, Double, Double, Boolean, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var farRadius by remember { mutableStateOf(initialFarRadius) }
    var mediumRadius by remember { mutableStateOf(initialMediumRadius) }
    var nearRadius by remember { mutableStateOf(initialNearRadius) }
    var notificationsEnabled by remember { mutableStateOf(initialNotificationsEnabled) }
    var soundEnabled by remember { mutableStateOf(initialSoundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(initialVibrationEnabled) }
    
    val radiusMin = 50.0
    val radiusMax = 1000.0
    val radiusStep = 50.0
    
    // Colores para cada radio (como iOS)
    val farColor = Color(0xFFFF6B6B) // Rojo
    val mediumColor = Color(0xFFFFD93D) // Amarillo
    val nearColor = Color(0xFF6BCF7F) // Verde
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🎯 Configurar Radios")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Descripción
                Text(
                    text = if (notificationsEnabled) {
                        "Personaliza las distancias de alerta"
                    } else {
                        "Activa las notificaciones para configurar"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // 🚨 Alerta Temprana (Far)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🚨 Alerta Temprana",
                            style = MaterialTheme.typography.bodyMedium,
                            color = farColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${farRadius.toInt()}m",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Slider(
                        value = farRadius.toFloat(),
                        onValueChange = { newValue ->
                            farRadius = newValue.toDouble()
                            // Ajustar jerarquía: far >= medium >= near
                            if (farRadius < mediumRadius) {
                                mediumRadius = farRadius
                            }
                            if (farRadius < nearRadius) {
                                nearRadius = farRadius
                            }
                        },
                        valueRange = radiusMin.toFloat()..radiusMax.toFloat(),
                        steps = ((radiusMax - radiusMin) / radiusStep).toInt() - 1,
                        enabled = notificationsEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = farColor,
                            activeTrackColor = farColor
                        )
                    )
                }
                
                // 🎯 Alerta Principal (Medium)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🎯 Alerta Principal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = mediumColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${mediumRadius.toInt()}m",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Slider(
                        value = mediumRadius.toFloat(),
                        onValueChange = { newValue ->
                            mediumRadius = newValue.toDouble()
                            // Ajustar jerarquía
                            if (mediumRadius > farRadius) {
                                farRadius = mediumRadius
                            }
                            if (mediumRadius < nearRadius) {
                                nearRadius = mediumRadius
                            }
                        },
                        valueRange = radiusMin.toFloat()..radiusMax.toFloat(),
                        steps = ((radiusMax - radiusMin) / radiusStep).toInt() - 1,
                        enabled = notificationsEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = mediumColor,
                            activeTrackColor = mediumColor
                        )
                    )
                }
                
                // 📍 Alerta Final (Near)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📍 Alerta Final",
                            style = MaterialTheme.typography.bodyMedium,
                            color = nearColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${nearRadius.toInt()}m",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Slider(
                        value = nearRadius.toFloat(),
                        onValueChange = { newValue ->
                            nearRadius = newValue.toDouble()
                            // Ajustar jerarquía
                            if (nearRadius > mediumRadius) {
                                mediumRadius = nearRadius
                            }
                            if (nearRadius > farRadius) {
                                farRadius = nearRadius
                            }
                        },
                        valueRange = radiusMin.toFloat()..radiusMax.toFloat(),
                        steps = ((radiusMax - radiusMin) / radiusStep).toInt() - 1,
                        enabled = notificationsEnabled,
                        colors = SliderDefaults.colors(
                            thumbColor = nearColor,
                            activeTrackColor = nearColor
                        )
                    )
                }
                
                HorizontalDivider()
                
                // Opciones de notificación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Notificaciones")
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sonido")
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it },
                        enabled = notificationsEnabled
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vibración")
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { vibrationEnabled = it },
                        enabled = notificationsEnabled
                    )
                }
                
                // Botón de reset
                OutlinedButton(
                    onClick = {
                        farRadius = 300.0
                        mediumRadius = 200.0
                        nearRadius = 100.0
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restaurar Valores")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfigChanged(
                        farRadius,
                        mediumRadius,
                        nearRadius,
                        notificationsEnabled,
                        soundEnabled,
                        vibrationEnabled
                    )
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
