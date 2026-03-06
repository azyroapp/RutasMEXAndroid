package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Vista de estado vacío
 * Muestra un icono, título y mensaje cuando no hay contenido
 */
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    iconColor: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = iconColor.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

/**
 * Estados vacíos predefinidos
 */
object EmptyStates {
    @Composable
    fun NoLocationSelected() {
        EmptyStateView(
            icon = Icons.Default.LocationSearching,
            title = "Selecciona origen o destino",
            message = "Toca los botones de arriba para elegir tu punto de origen o destino, luego verás las rutas disponibles.",
            iconColor = MaterialTheme.colorScheme.secondary
        )
    }
    
    @Composable
    fun NoRoutesAvailable() {
        EmptyStateView(
            icon = Icons.Default.Warning,
            title = "No hay rutas disponibles",
            message = "No se encontraron rutas cerca de tu ubicación. Prueba ajustar los radios de búsqueda.",
            iconColor = Color(0xFFFF9800) // Orange
        )
    }
    
    @Composable
    fun NoCitySelected() {
        EmptyStateView(
            icon = Icons.Default.LocationCity,
            title = "Selecciona una ciudad",
            message = "Primero debes seleccionar una ciudad para ver las rutas disponibles.",
            iconColor = MaterialTheme.colorScheme.primary
        )
    }
}
