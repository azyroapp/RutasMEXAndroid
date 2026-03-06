package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.ui.theme.RouteCompleto
import com.azyroapp.rutasmex.ui.theme.RouteIda
import com.azyroapp.rutasmex.ui.theme.RouteRegreso

/**
 * Componente para mostrar una ruta en la lista
 */
@Composable
fun RouteItem(
    route: Route,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información de la ruta
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = route.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                val segmentInfo = when {
                    route.coordinates.size > 1 -> "IDA Y REGRESO"
                    route.coordinates.firstOrNull()?.name?.contains("IDA", ignoreCase = true) == true -> "IDA"
                    route.coordinates.firstOrNull()?.name?.contains("REGRESO", ignoreCase = true) == true -> "REGRESO"
                    else -> "COMPLETO"
                }
                
                Text(
                    text = "$segmentInfo • ${route.totalPoints} paradas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Indicador de color de ruta
            val routeColor = when {
                route.coordinates.size == 1 && route.coordinates.first().name.contains("IDA", ignoreCase = true) -> RouteIda
                route.coordinates.size == 1 && route.coordinates.first().name.contains("REGRESO", ignoreCase = true) -> RouteRegreso
                else -> RouteCompleto
            }
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(routeColor)
            )
        }
    }
}
