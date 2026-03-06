package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.Trip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Modal de llegada al destino
 * Muestra estadísticas del viaje y opciones para finalizar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrivalModal(
    trip: Trip,
    onFinishTrip: () -> Unit,
    onSaveAsFavorite: () -> Unit,
    onShareTrip: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    // Animación de escala para el ícono de éxito
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono de éxito animado
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Título
            Text(
                text = "¡Has llegado!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Viaje completado exitosamente",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Estadísticas del viaje
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Resumen del Viaje",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Ruta
                    StatRow(
                        label = "Ruta",
                        value = trip.routeName,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Distancia
                    StatRow(
                        label = "Distancia recorrida",
                        value = "${String.format("%.2f", trip.totalDistance)} km",
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Duración
                    val duration = trip.duration ?: 0L
                    val hours = duration / 3600
                    val minutes = (duration % 3600) / 60
                    val durationText = when {
                        hours > 0 -> "${hours}h ${minutes}min"
                        else -> "${minutes}min"
                    }
                    
                    StatRow(
                        label = "Duración",
                        value = durationText,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Velocidad promedio
                    val avgSpeed = if (duration > 0) {
                        (trip.totalDistance / (duration / 3600.0))
                    } else {
                        0.0
                    }
                    
                    StatRow(
                        label = "Velocidad promedio",
                        value = "${avgSpeed.roundToInt()} km/h",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Hora de inicio
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    StatRow(
                        label = "Hora de inicio",
                        value = timeFormat.format(trip.startTime),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Hora de llegada
                    val endTime = trip.endTime ?: Date()
                    StatRow(
                        label = "Hora de llegada",
                        value = timeFormat.format(endTime),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Guardar como favorito
                OutlinedButton(
                    onClick = onSaveAsFavorite,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Favorito")
                }
                
                // Compartir
                OutlinedButton(
                    onClick = onShareTrip,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón principal de finalizar
            Button(
                onClick = onFinishTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Finalizar Viaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Fila de estadística
 */
@Composable
private fun StatRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
