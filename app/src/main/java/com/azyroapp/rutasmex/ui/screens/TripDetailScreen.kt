package com.azyroapp.rutasmex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.Trip
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de detalle de un viaje individual
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    trip: Trip,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Viaje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Mapa con la ruta
            TripMapCard(trip = trip)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información general
            TripInfoCard(trip = trip, dateFormat = dateFormat)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estadísticas detalladas
            TripStatisticsDetailCard(trip = trip)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ubicaciones
            TripLocationsCard(trip = trip)
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar viaje") },
            text = { Text("¿Estás seguro de que quieres eliminar este viaje? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Card con el mapa de la ruta
 */
@Composable
fun TripMapCard(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 16.dp)
    ) {
        val originLatLng = LatLng(trip.originLatitude, trip.originLongitude)
        val destinationLatLng = LatLng(trip.destinationLatitude, trip.destinationLongitude)
        
        // Calcular centro y zoom
        val centerLat = (trip.originLatitude + trip.destinationLatitude) / 2
        val centerLng = (trip.originLongitude + trip.destinationLongitude) / 2
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(centerLat, centerLng), 13f)
        }
        
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                compassEnabled = true,
                myLocationButtonEnabled = false
            )
        ) {
            // Marcador de origen
            Marker(
                state = MarkerState(position = originLatLng),
                title = "Origen",
                snippet = trip.originName
            )
            
            // Marcador de destino
            Marker(
                state = MarkerState(position = destinationLatLng),
                title = "Destino",
                snippet = trip.destinationName
            )
            
            // Línea entre origen y destino
            Polyline(
                points = listOf(originLatLng, destinationLatLng),
                color = MaterialTheme.colorScheme.primary,
                width = 5f
            )
        }
    }
}

/**
 * Card con información general del viaje
 */
@Composable
fun TripInfoCard(
    trip: Trip,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (trip.isCompleted) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (trip.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = trip.routeName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (trip.isCompleted) "Completado" else if (trip.isCancelled) "Cancelado" else "En progreso",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información de tiempo
            InfoRow(
                icon = Icons.Default.Schedule,
                label = "Inicio",
                value = dateFormat.format(trip.startTime)
            )
            
            if (trip.endTime != null) {
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.Schedule,
                    label = "Fin",
                    value = dateFormat.format(trip.endTime)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                icon = Icons.Default.Timer,
                label = "Duración",
                value = trip.getFormattedDuration()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(
                icon = Icons.Default.LocationCity,
                label = "Ciudad",
                value = trip.cityName
            )
        }
    }
}

/**
 * Card con estadísticas detalladas
 */
@Composable
fun TripStatisticsDetailCard(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticColumn(
                    icon = Icons.Default.Route,
                    label = "Distancia",
                    value = trip.getFormattedDistance()
                )
                
                StatisticColumn(
                    icon = Icons.Default.Speed,
                    label = "Velocidad",
                    value = String.format("%.0f km/h", trip.averageSpeed)
                )
                
                StatisticColumn(
                    icon = Icons.Default.DirectionsBus,
                    label = "Modo",
                    value = trip.calculationMode
                )
            }
        }
    }
}

/**
 * Columna de estadística
 */
@Composable
fun StatisticColumn(
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
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Card con ubicaciones
 */
@Composable
fun TripLocationsCard(
    trip: Trip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ubicaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Origen
            LocationItem(
                icon = Icons.Default.TripOrigin,
                label = "Origen",
                name = trip.originName,
                coordinates = "${trip.originLatitude}, ${trip.originLongitude}",
                iconTint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Destino
            LocationItem(
                icon = Icons.Default.LocationOn,
                label = "Destino",
                name = trip.destinationName,
                coordinates = "${trip.destinationLatitude}, ${trip.destinationLongitude}",
                iconTint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Item de ubicación
 */
@Composable
fun LocationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    name: String,
    coordinates: String,
    iconTint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = coordinates,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Fila de información
 */
@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
