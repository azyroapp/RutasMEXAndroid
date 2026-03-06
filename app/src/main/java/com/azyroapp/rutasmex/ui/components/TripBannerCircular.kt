package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Banner circular negro que muestra distancia y tiempo del viaje
 * Replica el TripBanner de iOS
 * 
 * Características:
 * - Círculo negro de 48x48 dp
 * - Muestra distancia (km) y tiempo (min)
 * - Tap para expandir info detallada (futuro)
 * - Placeholder cuando no hay datos
 */
@Composable
fun TripBannerCircular(
    distance: Double?,
    time: Int?,
    hasData: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "scale"
    )
    
    Surface(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
                isPressed = false
            },
        shape = CircleShape,
        color = Color.Black,
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (hasData && distance != null && time != null) {
                // Mostrar distancia y tiempo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                ) {
                    // Distancia
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(bottom = 1.dp)
                    ) {
                        Text(
                            text = String.format("%.1f", distance / 1000), // Convertir metros a km
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "km",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Tiempo
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = time.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "min",
                            fontSize = 8.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Placeholder cuando no hay datos
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = "Sin datos",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tap",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Preview del TripBannerCircular
 */
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun TripBannerCircularPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Con datos
            TripBannerCircular(
                distance = 12500.0, // 12.5 km
                time = 37,
                hasData = true,
                onClick = {}
            )
            
            // Sin datos
            TripBannerCircular(
                distance = null,
                time = null,
                hasData = false,
                onClick = {}
            )
        }
    }
}
