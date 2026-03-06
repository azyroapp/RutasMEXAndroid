package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azyroapp.rutasmex.data.model.LocationPoint

/**
 * Barra de entrada de ubicaciones con 4 botones
 * Replica exactamente el diseño de iOS
 */
@Composable
fun LocationInputRow(
    origenLocation: LocationPoint?,
    destinoLocation: LocationPoint?,
    onOriginTap: () -> Unit,
    onDestinationTap: () -> Unit,
    onSwap: () -> Unit,
    onFavoriteTap: () -> Unit,
    isTripActive: Boolean,
    hasCitySelected: Boolean,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    val hasAnyLocation = origenLocation != null || destinoLocation != null
    val hasBothLocations = origenLocation != null && destinoLocation != null
    
    // Botones deshabilitados si no hay ciudad O si hay viaje activo
    val areButtonsDisabled = !hasCitySelected || isTripActive

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de origen
        LocationButton(
            text = origenLocation?.name ?: "",
            placeholder = "Origen",
            position = LocationButtonPosition.LEADING,
            onClick = onOriginTap,
            enabled = !areButtonsDisabled,
            modifier = Modifier.weight(1f)
        )

        // Botones centrales
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Botón swap
            CircularIconButton(
                icon = Icons.Default.SwapHoriz,
                size = 20.dp,
                enabled = hasAnyLocation && hasCitySelected && !isTripActive,
                isFavorite = false,
                onClick = onSwap,
                rotateOnPress = true
            )

            // Botón favorito
            CircularIconButton(
                icon = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                size = 18.dp,
                enabled = hasBothLocations && hasCitySelected,
                isFavorite = isFavorite,
                onClick = onFavoriteTap,
                rotateOnPress = false
            )
        }

        // Botón de destino
        LocationButton(
            text = destinoLocation?.name ?: "",
            placeholder = "Destino",
            position = LocationButtonPosition.TRAILING,
            onClick = onDestinationTap,
            enabled = !areButtonsDisabled,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Posición del botón de ubicación (para formas asimétricas)
 */
enum class LocationButtonPosition {
    LEADING,  // Izquierda
    TRAILING  // Derecha
}

/**
 * Botón de ubicación con forma asimétrica
 */
@Composable
fun LocationButton(
    text: String,
    placeholder: String,
    position: LocationButtonPosition,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = when (position) {
        LocationButtonPosition.LEADING -> RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 5.dp,
            bottomEnd = 5.dp,
            bottomStart = 20.dp
        )
        LocationButtonPosition.TRAILING -> RoundedCornerShape(
            topStart = 5.dp,
            topEnd = 20.dp,
            bottomEnd = 20.dp,
            bottomStart = 5.dp
        )
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surface,  // Sin alpha para mejor visibilidad
        tonalElevation = 4.dp,  // Aumentado para mejor contraste
        shadowElevation = 6.dp  // Aumentado para mejor definición
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)  // Reducido de 12dp a 8dp
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (text.isEmpty()) placeholder.uppercase() else text,
                fontSize = if (text.isEmpty()) 10.sp else 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Botón circular con icono
 */
@Composable
fun CircularIconButton(
    icon: ImageVector,
    size: androidx.compose.ui.unit.Dp,
    enabled: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    rotateOnPress: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    // Animación de escala
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "scale"
    )
    
    // Animación de rotación (solo para swap)
    val rotation by animateFloatAsState(
        targetValue = if (rotateOnPress && isPressed) 180f else 0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "rotation"
    )

    // Efecto para resetear isPressed después de 100ms
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }

    Surface(
        onClick = {
            if (enabled) {
                isPressed = true
                onClick()
            }
        },
        enabled = enabled,
        modifier = modifier.size(50.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,  // Sin alpha para mejor visibilidad
        tonalElevation = 4.dp,  // Aumentado para mejor contraste
        shadowElevation = 6.dp  // Aumentado para mejor definición
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFavorite) {
                    Color(0xFFFFD700) // Amarillo dorado
                } else if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                modifier = Modifier
                    .size(size)
                    .scale(scale)
                    .rotate(rotation)
            )
        }
    }
}
