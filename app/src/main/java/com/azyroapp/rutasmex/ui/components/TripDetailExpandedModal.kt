package com.azyroapp.rutasmex.ui.components

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

/**
 * Modal con información detallada del viaje activo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailExpandedModal(
    distanceResult: RouteDistanceResult,
    routeName: String,
    onStopTrip: () -> Unit,
    onDismiss: () -> Unit
) {
    val estimatedTimeMinutes = (distanceResult.totalDistance * 3).toInt()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Viaje en Progreso")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre de la ruta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = null
                        )
                        Text(
                            text = routeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Distancia
                InfoRow(
                    icon = Icons.Default.Straighten,
                    label = "Distancia total",
                    value = "${String.format("%.2f", distanceResult.totalDistance)} km"
                )
                
                // Tiempo estimado
                InfoRow(
                    icon = Icons.Default.Schedule,
                    label = "Tiempo estimado",
                    value = "$estimatedTimeMinutes min"
                )
                
                // Modo de cálculo
                InfoRow(
                    icon = Icons.Default.Calculate,
                    label = "Modo de cálculo",
                    value = distanceResult.selectedMode.displayName()
                )
                
                // Velocidad promedio
                val avgSpeed = if (estimatedTimeMinutes > 0) {
                    (distanceResult.totalDistance / estimatedTimeMinutes) * 60
                } else {
                    0.0
                }
                InfoRow(
                    icon = Icons.Default.Speed,
                    label = "Velocidad promedio",
                    value = "${String.format("%.1f", avgSpeed)} km/h"
                )
                
                Divider()
                
                // Información adicional
                Text(
                    text = "Información del recorrido",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "• Mantén la app abierta para recibir notificaciones de proximidad",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "• El tiempo estimado es aproximado (3 min por km)",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "• Puedes detener el viaje en cualquier momento",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStopTrip()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Detener Viaje")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

/**
 * Fila de información con icono, label y valor
 */
@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
