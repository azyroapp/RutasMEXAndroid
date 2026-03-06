package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.City
import com.azyroapp.rutasmex.ui.theme.Primary
import com.azyroapp.rutasmex.ui.theme.PrimaryLight

/**
 * Componente para mostrar una ciudad en la lista
 */
@Composable
fun CityItem(
    city: City,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de ciudad
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = PrimaryLight
            ) {
                Icon(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = "Ciudad",
                    tint = Primary,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información de la ciudad
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${city.totalRoutes} rutas disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Icono de flecha
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "Seleccionar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
