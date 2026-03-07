package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.data.model.PlaceCategory
import com.azyroapp.rutasmex.data.model.SavedPlace
import kotlinx.coroutines.launch

/**
 * Modal para agregar o editar un lugar guardado
 * 
 * @param place Lugar existente a editar (null = agregar nuevo)
 * @param initialLocation Ubicación inicial desde el mapa (latLng, nombre)
 * @param onSave Callback al guardar (name, latitude, longitude, category)
 * @param onDismiss Callback al cerrar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlaceModal(
    place: SavedPlace? = null, // null = agregar nuevo, no-null = editar existente
    initialLocation: Pair<com.google.android.gms.maps.model.LatLng, String>? = null, // Desde mapa
    onSave: (name: String, latitude: Double, longitude: Double, category: PlaceCategory) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Si viene desde el mapa, usar esos datos
    val isFromMap = initialLocation != null && place == null
    val isEditingPlace = place != null // Editando lugar guardado
    val initialLat = initialLocation?.first?.latitude ?: place?.latitude
    val initialLon = initialLocation?.first?.longitude ?: place?.longitude
    
    // Estados
    var name by remember { mutableStateOf(place?.name ?: "") }
    var address by remember { mutableStateOf(place?.address ?: "") }
    var latitude by remember { mutableStateOf(initialLat?.toString() ?: "") }
    var longitude by remember { mutableStateOf(initialLon?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(place?.category ?: PlaceCategory.OTHER) }
    var isLoadingAddress by remember { mutableStateOf(false) }
    
    // Debug log
    android.util.Log.d("EditPlaceModal", "🎯 isFromMap: $isFromMap, isEditingPlace: $isEditingPlace, initialLocation: $initialLocation, place: $place")
    
    // ✅ GEOCODING AUTOMÁTICO (como iOS onAppear)
    LaunchedEffect(initialLocation) {
        if (initialLocation != null && address.isBlank()) {
            android.util.Log.d("EditPlaceModal", "🌍 Iniciando geocoding automático...")
            isLoadingAddress = true
            
            val result = com.azyroapp.rutasmex.core.services.GeocodingService.reverseGeocode(
                context = context,
                latitude = initialLocation.first.latitude,
                longitude = initialLocation.first.longitude
            )
            
            result.fold(
                onSuccess = { fetchedAddress ->
                    android.util.Log.d("EditPlaceModal", "✅ Dirección obtenida: $fetchedAddress")
                    address = fetchedAddress
                    isLoadingAddress = false
                },
                onFailure = { error ->
                    android.util.Log.e("EditPlaceModal", "❌ Error geocoding: ${error.message}")
                    address = "Dirección no disponible"
                    isLoadingAddress = false
                }
            )
        }
    }
    
    val isEditing = place != null
    val isValid = name.isNotBlank() && 
                  latitude.toDoubleOrNull() != null && 
                  longitude.toDoubleOrNull() != null
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con título y botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelar"
                    )
                }
                
                Text(
                    text = if (isEditing) "Editar Lugar" else "Agregar Lugar",
                    style = MaterialTheme.typography.titleLarge
                )
                
                TextButton(
                    onClick = {
                        if (isValid) {
                            onSave(
                                name,
                                latitude.toDouble(),
                                longitude.toDouble(),
                                selectedCategory
                            )
                            onDismiss()
                        }
                    },
                    enabled = isValid
                ) {
                    Text(if (isEditing) "Guardar" else "Agregar")
                }
            }
            
            Divider()
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nombre (siempre editable) ✏️
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del lugar") },
                    placeholder = { Text("Ej: Mi casa, Trabajo, Escuela") },
                    leadingIcon = {
                        Icon(Icons.Default.Place, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = true // ✅ EDITABLE
                )
                
                // Dirección (solo lectura) 🔒
                OutlinedTextField(
                    value = if (isLoadingAddress) "Cargando..." else address,
                    onValueChange = {}, // No hace nada
                    label = { Text("Dirección") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    trailingIcon = {
                        if (isLoadingAddress) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 2,
                    enabled = false, // ✅ SOLO LECTURA
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                // Latitud (solo lectura) 🔒
                OutlinedTextField(
                    value = latitude,
                    onValueChange = {}, // No hace nada
                    label = { Text("Latitud") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false, // ✅ SOLO LECTURA
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                // Longitud (solo lectura) 🔒
                OutlinedTextField(
                    value = longitude,
                    onValueChange = {}, // No hace nada
                    label = { Text("Longitud") },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false, // ✅ SOLO LECTURA
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                if (!isValid && name.isNotBlank()) {
                    Text(
                        text = "Por favor verifica las coordenadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                // Nota informativa
                if (isFromMap || isEditingPlace) {
                    Text(
                        text = "Solo puedes modificar el nombre del lugar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Obtiene el nombre para mostrar de una categoría
 */
private fun getCategoryDisplayName(category: PlaceCategory): String {
    return when (category) {
        PlaceCategory.HOME -> "Casa"
        PlaceCategory.WORK -> "Trabajo"
        PlaceCategory.SCHOOL -> "Escuela"
        PlaceCategory.FAVORITE -> "Favorito"
        PlaceCategory.OTHER -> "Otro"
    }
}

/**
 * Obtiene el icono de una categoría
 */
private fun getCategoryIcon(category: PlaceCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        PlaceCategory.HOME -> Icons.Default.Home
        PlaceCategory.WORK -> Icons.Default.Work
        PlaceCategory.SCHOOL -> Icons.Default.School
        PlaceCategory.FAVORITE -> Icons.Default.Star
        PlaceCategory.OTHER -> Icons.Default.Place
    }
}
