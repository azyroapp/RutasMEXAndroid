package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.background
import kotlin.math.absoluteValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.azyroapp.rutasmex.core.services.GeocodingService
import com.azyroapp.rutasmex.data.model.PlaceCategory
import com.azyroapp.rutasmex.data.model.SavedPlace
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * Modal para gestionar lugares guardados
 * Paridad 100% con iOS SavedPlacesManagerModal
 * 
 * Características:
 * - Modo lista: Muestra lugares guardados
 * - Modo agregar: Mapa interactivo para seleccionar ubicación
 * - Snackbar informativo al entrar en modo agregar
 * - Círculos con iniciales y colores dinámicos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesManagerModal(
    places: List<SavedPlace>,
    onPlaceSelected: (SavedPlace) -> Unit,
    onDeletePlace: (SavedPlace) -> Unit,
    onEditPlace: (SavedPlace) -> Unit,
    onAddPlace: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Estado: modo lista o modo agregar
    var isAddingPlace by remember { mutableStateOf(false) }
    var selectedCoordinate by remember { mutableStateOf<LatLng?>(null) }
    var showPlaceEditor by remember { mutableStateOf(false) }
    var locationFromMap by remember { mutableStateOf<Pair<LatLng, String>?>(null) }
    var isGeocodingInProgress by remember { mutableStateOf(false) }
    
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Mostrar snackbar al entrar en modo agregar
    LaunchedEffect(isAddingPlace) {
        if (isAddingPlace) {
            snackbarHostState.showSnackbar(
                message = "Toca el mapa para marcar tu lugar",
                duration = SnackbarDuration.Indefinite
            )
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = {
            if (!isAddingPlace) {
                onDismiss()
            }
        },
        modifier = modifier.fillMaxHeight()
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            topBar = {
                // Top bar con título y botones
                TopAppBar(
                    title = {
                        Text(
                            text = if (isAddingPlace) "Seleccionar Ubicación" else "Mis Lugares",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isAddingPlace) {
                                // Cancelar modo agregar
                                isAddingPlace = false
                                selectedCoordinate = null
                            } else {
                                // Cerrar modal
                                onDismiss()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isAddingPlace) "Cancelar" else "Cerrar"
                            )
                        }
                    },
                    actions = {
                        if (!isAddingPlace) {
                            IconButton(onClick = {
                                isAddingPlace = true
                                selectedCoordinate = null
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Agregar lugar"
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isAddingPlace) {
                    // Modo agregar: Mapa interactivo
                    Box(modifier = Modifier.fillMaxSize()) {
                        MapForPlaceSelection(
                            selectedCoordinate = selectedCoordinate,
                            onMapClick = { latLng ->
                                android.util.Log.d("SavedPlacesManagerModal", "🗺️ Mapa tocado en: ${latLng.latitude}, ${latLng.longitude}")
                                selectedCoordinate = latLng
                                isGeocodingInProgress = true
                                
                                // Hacer geocoding reverso antes de abrir el modal
                                coroutineScope.launch {
                                    android.util.Log.d("SavedPlacesManagerModal", "🔄 Iniciando geocoding...")
                                    val result = GeocodingService.reverseGeocode(
                                        context = context,
                                        latitude = latLng.latitude,
                                        longitude = latLng.longitude
                                    )
                                    
                                    result.fold(
                                        onSuccess = { address ->
                                            android.util.Log.d("SavedPlacesManagerModal", "✅ Geocoding exitoso: $address")
                                            locationFromMap = Pair(latLng, address)
                                            showPlaceEditor = true
                                            isGeocodingInProgress = false
                                        },
                                        onFailure = { error ->
                                            android.util.Log.e("SavedPlacesManagerModal", "❌ Geocoding falló: ${error.message}")
                                            // Si falla el geocoding, usar coordenadas como dirección
                                            val fallbackAddress = "Lat: ${String.format("%.6f", latLng.latitude)}, Lon: ${String.format("%.6f", latLng.longitude)}"
                                            locationFromMap = Pair(latLng, fallbackAddress)
                                            showPlaceEditor = true
                                            isGeocodingInProgress = false
                                        }
                                    )
                                }
                            }
                        )
                        
                        // Indicador de geocoding en progreso
                        if (isGeocodingInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    // Modo lista: Lugares guardados
                    PlacesList(
                        places = places,
                        onPlaceSelected = {
                            onPlaceSelected(it)
                            onDismiss()
                        },
                        onEditPlace = onEditPlace,
                        onDeletePlace = onDeletePlace
                    )
                }
            }
        }
    }
    
    // Modal de editor de lugar
    if (showPlaceEditor && locationFromMap != null) {
        EditPlaceModal(
            place = null, // Nuevo lugar
            initialLocation = locationFromMap, // Pasar ubicación con dirección desde geocoding
            onSave = { name, lat, lon, category ->
                // Crear nuevo lugar
                val newPlace = SavedPlace.create(
                    name = name,
                    latitude = lat,
                    longitude = lon,
                    category = category
                )
                onPlaceSelected(newPlace)
                
                // Salir del modo agregar
                isAddingPlace = false
                selectedCoordinate = null
                locationFromMap = null
                showPlaceEditor = false
            },
            onDismiss = {
                showPlaceEditor = false
                locationFromMap = null
            }
        )
    }
}

/**
 * Mapa para seleccionar ubicación de nuevo lugar
 */
@Composable
private fun MapForPlaceSelection(
    selectedCoordinate: LatLng?,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    // Posición inicial: Tuxtla Gutiérrez
    val tuxtla = LatLng(16.7504034, -93.12392021)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tuxtla, 13f)
    }
    
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = onMapClick
    ) {
        // Mostrar marcador si hay coordenada seleccionada
        selectedCoordinate?.let { coordinate ->
            Marker(
                state = MarkerState(position = coordinate),
                title = "Nuevo lugar"
            )
        }
    }
}

/**
 * Lista de lugares guardados
 */
@Composable
private fun PlacesList(
    places: List<SavedPlace>,
    onPlaceSelected: (SavedPlace) -> Unit,
    onEditPlace: (SavedPlace) -> Unit,
    onDeletePlace: (SavedPlace) -> Unit,
    modifier: Modifier = Modifier
) {
    if (places.isEmpty()) {
        // Estado vacío
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "No tienes lugares guardados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Toca el botón + para agregar tu primer lugar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // Lista de lugares
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(places) { place ->
                PlaceRow(
                    place = place,
                    onSelect = { onPlaceSelected(place) },
                    onEdit = { onEditPlace(place) },
                    onDelete = { onDeletePlace(place) }
                )
            }
        }
    }
}

/**
 * Fila de lugar guardado con círculo de iniciales
 */
@Composable
private fun PlaceRow(
    place: SavedPlace,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    // Generar iniciales
    val initials = remember(place.name) {
        val words = place.name.split(" ").filter { it.isNotEmpty() }
        when {
            words.size >= 2 -> {
                "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
            }
            words.size == 1 -> {
                words[0].take(2).uppercase()
            }
            else -> "??"
        }
    }
    
    // Color dinámico basado en el hash del ID
    val dynamicColor = remember(place.id) {
        val colors = listOf(
            Color(0xFF2196F3), // Blue
            Color(0xFF4CAF50), // Green
            Color(0xFFFF9800), // Orange
            Color(0xFF9C27B0), // Purple
            Color(0xFFF44336), // Red
            Color(0xFFE91E63), // Pink
            Color(0xFF00BCD4), // Teal
            Color(0xFF3F51B5)  // Indigo
        )
        val colorIndex = place.id.hashCode().absoluteValue % colors.size
        colors[colorIndex]
    }
    
    Surface(
        onClick = onSelect,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con iniciales
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = dynamicColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = dynamicColor
                )
            }
            
            // Información del lugar
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (place.address != null) {
                    Text(
                        text = place.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Icono de chevron
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            },
            title = {
                Text("¿Eliminar lugar?")
            },
            text = {
                Text("Se eliminará \"${place.name}\" de tus lugares guardados")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
