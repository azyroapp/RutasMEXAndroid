package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azyroapp.rutasmex.data.model.DistanceCalculationMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Botón para cambiar el modo de cálculo de distancia
 * Muestra IDA, REGRESO o COMPLETO con colores distintivos
 */
@Composable
fun CalculationModeButton(
    currentMode: DistanceCalculationMode,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Scope para coroutines
    val scope = rememberCoroutineScope()
    
    // Animación de escala al hacer tap
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    // Color según el modo
    val backgroundColor = when (currentMode) {
        DistanceCalculationMode.IDA -> Color(0xFF00C3FF)      // Azul
        DistanceCalculationMode.REGRESO -> Color(0xFFFF6B00)  // Naranja
        DistanceCalculationMode.COMPLETO -> Color(0xFF9C27B0) // Morado
    }
    
    Box(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable {
                isPressed = true
                onToggleMode()
                // Reset después de un delay
                scope.launch {
                    delay(100)
                    isPressed = false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (currentMode) {
                DistanceCalculationMode.IDA -> "IDA"
                DistanceCalculationMode.REGRESO -> "REG"
                DistanceCalculationMode.COMPLETO -> "COM"
            },
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
