package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.RouteDistanceResult

/**
 * Barra de controles del mapa (arriba del modal persistente)
 * Replica MapControlsBar de iOS
 */
@Composable
fun MapControlsBar(
    isTripActive: Boolean,
    hasCity: Boolean,
    hasOrigin: Boolean,
    hasDestination: Boolean,
    hasSelectedRoutes: Boolean,
    distanceResult: RouteDistanceResult?,
    onPlayTrip: () -> Unit,
    onStopTrip: () -> Unit,
    onReset: () -> Unit,
    onConfigureRadius: () -> Unit,
    onMapSelection: () -> Unit,
    onSearch: () -> Unit,
    onTripBannerClick: () -> Unit = {},
    onChangeOrigin: () -> Unit = {},
    onChangeDestination: () -> Unit = {},
    onNewOriginDestination: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Trip Banner Circular (si hay viaje activo)
        if (isTripActive && distanceResult != null) {
            TripBannerCircular(
                distance = distanceResult.distanceToDestination,
                time = (distanceResult.distanceToDestination / 1000 * 3).toInt(), // Estimación: 3 min por km
                hasData = true,
                onClick = onTripBannerClick
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Play / Stop
        if (!isTripActive) {
            // Play - Iniciar viaje
            MapControlButton(
                icon = Icons.Default.PlayArrow,
                enabled = hasOrigin && hasDestination && hasSelectedRoutes && hasCity,
                onClick = onPlayTrip
            )
        } else {
            // Stop - Detener viaje
            MapControlButton(
                icon = Icons.Default.Stop,
                enabled = true,
                tint = MaterialTheme.colorScheme.error,
                onClick = onStopTrip
            )
        }
        
        // Reset
        MapControlButton(
            icon = Icons.Default.Delete,
            enabled = !isTripActive && hasCity,
            onClick = onReset
        )
        
        // Radio
        MapControlButton(
            icon = Icons.Default.Settings,
            enabled = !isTripActive && hasCity,
            onClick = onConfigureRadius
        )
        
        // Selección en mapa (con menú si hay ubicaciones)
        if (hasOrigin || hasDestination) {
            // Con ubicaciones: mostrar menú
            var showMenu by remember { mutableStateOf(false) }
            
            Box {
                MapControlButton(
                    icon = Icons.Default.MyLocation,
                    enabled = !isTripActive && hasCity,
                    onClick = { showMenu = true }
                )
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nuevo origen y destino") },
                        onClick = {
                            onNewOriginDestination()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        enabled = !isTripActive
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Cambiar origen") },
                        onClick = {
                            onChangeOrigin()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null)
                        },
                        enabled = hasDestination && !isTripActive
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Cambiar destino") },
                        onClick = {
                            onChangeDestination()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.FmdGood, contentDescription = null)
                        },
                        enabled = hasOrigin && !isTripActive
                    )
                }
            }
        } else {
            // Sin ubicaciones: tap directo
            MapControlButton(
                icon = Icons.Default.MyLocation,
                enabled = !isTripActive && hasCity,
                onClick = onMapSelection
            )
        }
        
        // Búsqueda
        MapControlButton(
            icon = Icons.Default.Search,
            enabled = hasCity,
            onClick = onSearch
        )
    }
}

/**
 * Botón de control del mapa con efecto glass
 */
@Composable
private fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "scale"
    )
    
    Surface(
        onClick = {
            if (enabled) {
                isPressed = true
                onClick()
                isPressed = false
            }
        },
        modifier = Modifier
            .size(48.dp)
            .scale(scale),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) tint else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
