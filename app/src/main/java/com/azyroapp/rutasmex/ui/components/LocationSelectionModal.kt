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
 * - Lista scrolleable de sugerencias
 * - Sección "Mis Lugares" cuando no hay búsqueda
 * - Validación de ubicaciones duplicadas (< 20 metros)
 * - Botones OK y Cancelar en toolbar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionModal(
    isSelectingOrigin: Boolean,
    currentLocation: LocationPoint?,
    savedPlaces: List<LocationPoint>,
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
    var suggestions by remember { mutableStateOf<List<LocationPoint>>(emptyList()) }
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
                            // TODO: Actualizar suggestions con resultados
                        } else {
                            suggestions = emptyList()
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
                                suggestions = emptyList()
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
            
            // Lista scrolleable de sugerencias o Mis Lugares
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (suggestions.isNotEmpty()) {
                    // Mostrar resultados de búsqueda
                    items(suggestions) { suggestion ->
                        LocationSuggestionRow(
                            location = suggestion,
                            onClick = {
                                if (validateLocation(suggestion)) {
                                    selectedLocation = suggestion
                                    searchText = suggestion.name
                                    suggestions = emptyList()
                                    onLocationSelected(suggestion)
                                    onDismiss()
                                }
                            }
                        )
                    }
                } else if (searchText.isEmpty() && savedPlaces.isNotEmpty()) {
                    // Mostrar "Mis Lugares" cuando no hay búsqueda
                    item {
                        Text(
                            text = "Mis Lugares",
                            style = MaterialTheme.typography.titleMedium,
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
