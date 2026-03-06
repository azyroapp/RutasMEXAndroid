package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.azyroapp.rutasmex.data.model.Route

/**
 * Modal inferior persistente
 * Siempre visible en la parte inferior de la pantalla
 * Replica el comportamiento de iOS PersistentBottomModal
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
    // Filtrar rutas según origen/destino
    val filteredRoutes = remember(routes, origenLocation, destinoLocation) {
        filterRoutes(routes, origenLocation, destinoLocation)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp)
        ) {
            // Drag handle visual
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barra de control (LocationInputRow)
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
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Contenido dinámico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp)
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
                                .fillMaxWidth()
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
    
    // TODO: Implementar filtrado real basado en proximidad
    // Por ahora retornamos todas las rutas
    return routes
}
