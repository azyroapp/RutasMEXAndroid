package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.LocationPoint

/**
 * Modal para seleccionar ubicación (origen o destino)
 * Replica EXACTAMENTE el comportamiento de iOS LocationSelectionModal
 * 
 * Características:
 * - Campo de búsqueda con sugerencias en tiempo real
 * - Botón "Mi Ubicación" como icono
 * - Lista scrolleable que muestra:
 *   1. "Mis Lugares" (siempre visible si hay lugares guardados)
 *   2. "Resultados de búsqueda" (cuando hay búsqueda activa)
 * - Validación de ubicaciones duplicadas (< 20 metros)
 * - Botones OK y Cancelar en toolbar
 * 
 * COMPORTAMIENTO iOS:
 * - Sin búsqueda: Solo muestra "Mis Lugares"
 * - Con búsqueda: Muestra "Mis Lugares" + "Resultados de búsqueda"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionModal(
    isSelectingOrigin: Boolean,
    currentLocation: LocationPoint?,
    savedPlaces: List<LocationPoint>,
    searchResults: List<LocationPoint>,  // Resultados de búsqueda del ViewModel
    origenLocation: LocationPoint?,
    destinoLocation: LocationPoint?,
    onLocationSelected: (LocationPoint) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onSearchPlace: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(currentLocation?.name ?: "") }
    var selectedLocation by remember { mutableStateOf(currentLocation) }
    var isLoadingLocation by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Título según si es origen o destino
    val titleText = if (isSelectingOrigin) "📍 Seleccionar Origen" else "🎯 Seleccionar Destino"
    val searchPlaceholder = if (isSelectingOrigin) "Buscar lugar de origen..." else "Buscar lugar de destino..."
    
    /**
     * Valida que la ubicación seleccionada no esté muy cerca de la otra ubicación
     * Usa tolerancia de ~20 metros (0.00018 grados)
     */
    fun validateLocation(location: LocationPoint): Boolean {
        keyboardController?.hide()
        
        val otherLocation = if (isSelectingOrigin) destinoLocation else origenLocation
        
        if (otherLocation != null) {
            val latDiff = kotlin.math.abs(location.latitude - otherLocation.latitude)
            val lonDiff = kotlin.math.abs(location.longitude - otherLocation.longitude)
            
            if (latDiff < 0.00018 && lonDiff < 0.00018) {
                // TODO: Mostrar Toast de advertencia
                // "Ubicaciones muy cercanas - El origen y destino están a menos de 20 metros"
                return false
            }
        }
        
        return true
    }
    
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
            // Header con título y botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Cancelar
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Título
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Botón OK
                TextButton(
                    onClick = {
                        selectedLocation?.let { location ->
                            if (validateLocation(location)) {
                                onLocationSelected(location)
                                onDismiss()
                            }
                        }
                    },
                    enabled = selectedLocation != null
                ) {
                    Text("OK")
                }
            }
            
            // Campo de búsqueda + Botón Mi Ubicación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo de búsqueda
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        // Buscar sugerencias si hay más de 2 caracteres
                        if (it.length > 2) {
                            onSearchPlace(it)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(searchPlaceholder) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { 
                                searchText = ""
                                onSearchPlace("")  // Limpiar resultados
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Limpiar"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            if (searchText.length > 2) {
                                onSearchPlace(searchText)
                            }
                        }
                    ),
                    singleLine = true
                )
                
                // Botón Mi Ubicación (icono)
                IconButton(
                    onClick = {
                        isLoadingLocation = true
                        onUseCurrentLocation()
                        // TODO: Cuando se obtenga la ubicación, validar y cerrar
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    if (isLoadingLocation) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Lista scrolleable: SIEMPRE muestra Mis Lugares + Resultados de búsqueda (como iOS)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // SECCIÓN 1: Mis Lugares (siempre visible si hay lugares guardados)
                if (savedPlaces.isNotEmpty()) {
                    item {
                        Text(
                            text = "Mis Lugares",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(savedPlaces) { place ->
                        SavedPlaceRow(
                            place = place,
                            onClick = {
                                if (validateLocation(place)) {
                                    selectedLocation = place
                                    onLocationSelected(place)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
                
                // SECCIÓN 2: Resultados de búsqueda (solo si hay búsqueda activa)
                if (searchText.isNotEmpty() && searchResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Resultados de búsqueda",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    
                    items(searchResults) { suggestion ->
                        LocationSuggestionRow(
                            location = suggestion,
                            onClick = {
                                if (validateLocation(suggestion)) {
                                    selectedLocation = suggestion
                                    searchText = suggestion.name
                                    onLocationSelected(suggestion)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
                
                // Mensaje si no hay resultados de búsqueda
                if (searchText.isNotEmpty() && searchResults.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron resultados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fila de sugerencia de ubicación (resultados de búsqueda)
 * Replica el diseño de iOS LocationSuggestionRow
 */
@Composable
private fun LocationSuggestionRow(
    location: LocationPoint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Mostrar dirección si está disponible
                location.address?.let { address ->
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Fila de lugar guardado
 * Replica el diseño de iOS SavedPlaceRow
 */
@Composable
private fun SavedPlaceRow(
    place: LocationPoint,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 2.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
                
                place.address?.let { address ->
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
