package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azyroapp.rutasmex.data.model.Route

/**
 * Grid adaptativo de rutas
 * Replica el comportamiento de iOS:
 * - Rutas con texto: 1-3 columnas adaptativas
 * - Rutas numéricas: 4 columnas fijas
 */
@Composable
fun RouteGrid(
    routes: List<Route>,
    selectedRouteIds: Set<String>,
    onRouteToggle: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    // Separar rutas por tipo
    val textRoutes = routes.filter { !it.isNumericRoute() }
        .sortedBy { it.name }
    
    val numericRoutes = routes.filter { it.isNumericRoute() }
        .sortedBy { it.getNumericValue() }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Rutas con texto completo (adaptativo: 1-3 columnas)
        if (textRoutes.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(textRoutes) { route ->
                    RouteChip(
                        route = route,
                        isSelected = selectedRouteIds.contains(route.id),
                        onClick = { onRouteToggle(route) }
                    )
                }
            }
        }

        // Rutas numéricas (4 columnas fijas)
        if (numericRoutes.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(numericRoutes) { route ->
                    RouteChip(
                        route = route,
                        isSelected = selectedRouteIds.contains(route.id),
                        onClick = { onRouteToggle(route) },
                        isCompact = true
                    )
                }
            }
        }
    }
}

/**
 * Chip de ruta individual
 */
@Composable
fun RouteChip(
    route: Route,
    isSelected: Boolean,
    onClick: () -> Unit,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = route.getDisplayName(),
                fontSize = if (isCompact) 12.sp else 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (isCompact) 40.dp else 48.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * Extensión para obtener el valor numérico de una ruta
 */
fun Route.getNumericValue(): Int {
    val displayName = this.getDisplayName()
    return displayName.toIntOrNull() ?: 0
}
