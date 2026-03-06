package com.azyroapp.rutasmex.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.azyroapp.rutasmex.data.model.Route

/**
 * Estados del BottomSheet expandible
 */
enum class BottomSheetState {
    COLLAPSED,  // Colapsado: 80dp (solo botones)
    EXPANDED    // Expandido: 50% pantalla (botones + rutas)
}

/**
 * Modal inferior persistente con dos estados: colapsado y expandido
 * - Colapsado: 80dp (solo botones de origen/destino)
 * - Expandido: 50% pantalla (botones + lista de rutas)
 * - Drag gesture nativo para deslizar
 * - No bloquea el contenido de atrás
 */
@Composable
fun PersistentBottomSheet(
    routes: List<Route>,
    selectedRouteIds: Set<String>,
    origenLocation: LocationPoint?,
    destinoLocation: LocationPoint?,
    isTripActive: Boolean,
    hasCitySelected: Boolean,
    isFavorite: Boolean,
    onRouteToggle: (Route) -> Unit,
    onOriginTap: () -> Unit,
    onDestinationTap: () -> Unit,
    onSwap: () -> Unit,
    onFavoriteTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado del sheet (colapsado o expandido)
    var sheetState by remember { mutableStateOf(BottomSheetState.COLLAPSED) }
    
    // Estado para el drag gesture
    var dragOffset by remember { mutableStateOf(0f) }
    
    // Filtrar rutas según origen/destino
    val filteredRoutes = remember(routes, origenLocation, destinoLocation) {
        filterRoutes(routes, origenLocation, destinoLocation)
    }
    
    // Configuración de pantalla
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    // Alturas según el estado
    val collapsedHeight = 80.dp // Solo botones
    val expandedHeight = screenHeight * 0.5f // 50% de la pantalla
    
    // Umbral para cambiar de estado (30% de la diferencia)
    val dragThreshold = ((expandedHeight - collapsedHeight) * 0.3f).value
    
    // Altura animada
    val animatedHeight by animateDpAsState(
        targetValue = when (sheetState) {
            BottomSheetState.COLLAPSED -> collapsedHeight
            BottomSheetState.EXPANDED -> expandedHeight
        },
        animationSpec = tween(durationMillis = 300),
        label = "bottomSheetHeight"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight),
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = androidx.compose.foundation.shape.CornerSize(0.dp),
            bottomEnd = androidx.compose.foundation.shape.CornerSize(0.dp)
        ),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 16.dp)
        ) {
            // Drag handle con gesture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                // Determinar nuevo estado según el offset acumulado
                                when (sheetState) {
                                    BottomSheetState.COLLAPSED -> {
                                        // Deslizar hacia arriba (negativo) para expandir
                                        if (dragOffset < -dragThreshold) {
                                            sheetState = BottomSheetState.EXPANDED
                                        }
                                    }
                                    BottomSheetState.EXPANDED -> {
                                        // Deslizar hacia abajo (positivo) para colapsar
                                        if (dragOffset > dragThreshold) {
                                            sheetState = BottomSheetState.COLLAPSED
                                        }
                                    }
                                }
                                // Reset del offset
                                dragOffset = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                // Acumular el drag
                                dragOffset += dragAmount
                            }
                        )
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )
            }
            
            // Barra de control (LocationInputRow) - SIEMPRE VISIBLE
            LocationInputRow(
                origenLocation = origenLocation,
                destinoLocation = destinoLocation,
                onOriginTap = onOriginTap,
                onDestinationTap = onDestinationTap,
                onSwap = onSwap,
                onFavoriteTap = onFavoriteTap,
                isTripActive = isTripActive,
                hasCitySelected = hasCitySelected,
                isFavorite = isFavorite,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Contenido dinámico - SOLO VISIBLE EN ESTADO EXPANDIDO
            if (sheetState == BottomSheetState.EXPANDED) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when {
                        // Sin ciudad seleccionada
                        !hasCitySelected -> {
                            EmptyStates.NoCitySelected()
                        }
                        
                        // Sin origen ni destino
                        origenLocation == null && destinoLocation == null -> {
                            EmptyStates.NoLocationSelected()
                        }
                        
                        // Sin rutas disponibles
                        filteredRoutes.isEmpty() -> {
                            EmptyStates.NoRoutesAvailable()
                        }
                        
                        // Mostrar rutas
                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                RouteGrid(
                                    routes = filteredRoutes,
                                    selectedRouteIds = selectedRouteIds,
                                    onRouteToggle = onRouteToggle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Filtra rutas según origen y destino seleccionados
 */
private fun filterRoutes(
    routes: List<Route>,
    origen: LocationPoint?,
    destino: LocationPoint?
): List<Route> {
    // Sin origen ni destino: sin rutas
    if (origen == null && destino == null) {
        return emptyList()
    }
    
    // Radios de búsqueda por defecto (200 metros)
    val searchRadius = 200.0
    
    return routes.filter { route ->
        val passesOrigin = origen?.let { 
            route.passesNearPoint(it.latitude, it.longitude, searchRadius)
        } ?: true
        
        val passesDestination = destino?.let {
            route.passesNearPoint(it.latitude, it.longitude, searchRadius)
        } ?: true
        
        // La ruta debe pasar cerca de ambos puntos (si están definidos)
        passesOrigin && passesDestination
    }
}
