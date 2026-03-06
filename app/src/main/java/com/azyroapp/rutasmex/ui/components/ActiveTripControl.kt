package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.RouteDistanceResult
import com.azyroapp.rutasmex.data.model.Trip

/**
 * Control para viaje activo
 * Muestra información del viaje en curso y botones de control
 */
@Composable
fun ActiveTripControl(
    isTripActive: Boolean,
    currentTrip: Trip?,
    distanceResult: RouteDistanceResult?,
    onStartTrip: () -> Unit,
    onStopTrip: () -> Unit,
    onCancelTrip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    
    AnimatedVisibility(
        visible = isTripActive && currentTrip != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Título
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Viaje en Curso",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentTrip?.routeName ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Información de distancia
                if (distanceResult != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TripInfoItem(
                            icon = Icons.Default.Route,
                            label = "Distancia",
                            value = String.format("%.1f km", distanceResult.totalDistance / 1000)
                        )
                        
                        TripInfoItem(
                            icon = Icons.Default.Speed,
                            label = "Modo",
                            value = distanceResult.selectedMode.name
                        )
                        
                        TripInfoItem(
                            icon = Icons.Default.Timer,
                            label = "Tiempo",
                            value = currentTrip?.getFormattedDuration() ?: "0:00"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Botones de control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Cancelar
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancelar")
                    }
                    
                    // Botón Completar
                    Button(
                        onClick = onStopTrip,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Completar")
                    }
                }
            }
        }
    }
    
    // Botón para iniciar viaje (cuando no hay viaje activo)
    AnimatedVisibility(
        visible = !isTripActive && distanceResult != null,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Button(
            onClick = onStartTrip,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar Viaje")
        }
    }
    
    // Diálogo de confirmación para cancelar
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar Viaje") },
            text = { Text("¿Estás seguro de que quieres cancelar este viaje? Se guardará como cancelado en el historial.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelTrip()
                        showCancelDialog = false
                    }
                ) {
                    Text("Cancelar Viaje", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Volver")
                }
            }
        )
    }
}

/**
 * Item de información del viaje
 */
@Composable
fun TripInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
