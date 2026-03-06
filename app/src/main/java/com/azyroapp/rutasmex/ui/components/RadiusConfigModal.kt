package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Modal para configurar radios de búsqueda
 * Replica el comportamiento de iOS RadiusConfigModal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadiusConfigModal(
    initialOriginRadius: Double,
    initialDestinationRadius: Double,
    onRadiiChanged: (originRadius: Double, destinationRadius: Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var originRadius by remember { mutableStateOf(initialOriginRadius.toFloat()) }
    var destinationRadius by remember { mutableStateOf(initialDestinationRadius.toFloat()) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // Título
            Text(
                text = "Configurar Radios de Búsqueda",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Ajusta la distancia máxima para buscar rutas cerca de cada punto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Radio de origen
            RadiusSlider(
                label = "Radio de Origen",
                icon = Icons.Default.TripOrigin,
                value = originRadius,
                onValueChange = { originRadius = it },
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Radio de destino
            RadiusSlider(
                label = "Radio de Destino",
                icon = Icons.Default.Place,
                value = destinationRadius,
                onValueChange = { destinationRadius = it },
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Vista previa visual
            RadiusPreview(
                originRadius = originRadius,
                destinationRadius = destinationRadius,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        originRadius = 200f
                        destinationRadius = 200f
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Restablecer")
                }
                
                Button(
                    onClick = {
                        onRadiiChanged(originRadius.toDouble(), destinationRadius.toDouble())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aplicar")
                }
            }
        }
    }
}

/**
 * Slider para configurar radio
 */
@Composable
private fun RadiusSlider(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Etiqueta con valor
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${value.roundToInt()} m",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
        
        // Slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 50f..1000f,
            steps = 18, // 50, 100, 150, ..., 1000
            modifier = Modifier.fillMaxWidth()
        )
        
        // Indicadores de rango
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "50 m",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "1000 m",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Vista previa visual de los radios
 */
@Composable
private fun RadiusPreview(
    originRadius: Float,
    destinationRadius: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Vista Previa",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Origen
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TripOrigin,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Origen",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${originRadius.roundToInt()} m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Separador visual
                HorizontalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(80.dp)
                )
                
                // Destino
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Destino",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${destinationRadius.roundToInt()} m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Las rutas deben pasar dentro de estos radios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
