package com.azyroapp.rutasmex.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.ui.components.*
import com.azyroapp.rutasmex.ui.viewmodel.HomeViewModel
import com.google.android.gms.maps.model.LatLng

/**
 * Pantalla principal de la aplicación con Compose
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Estados del ViewModel
    val cities by viewModel.cities.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    val availableRoutes by viewModel.availableRoutes.collectAsState()
    val selectedRoutes by viewModel.selectedRoutes.collectAsState()
    val origenLocation by viewModel.origenLocation.collectAsState()
    val destinoLocation by viewModel.destinoLocation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val mapType by viewModel.mapType.collectAsState()
    val foundRoutes by viewModel.foundRoutes.collectAsState()
    val isTripActive by viewModel.isTripActive.collectAsState()
    val currentTrip by viewModel.currentTrip.collectAsState()
    val distanceResult by viewModel.distanceResult.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val savedPlaces by viewModel.savedPlacesAsLocationPoints.collectAsState()
    val favoriteSearches by viewModel.favoriteSearches.collectAsState()
    val originRadius by viewModel.originRadius.collectAsState()
    val destinationRadius by viewModel.destinationRadius.collectAsState()
    val proximityConfig by viewModel.proximityConfig.collectAsState()
    
    // Estados locales para bottom sheets
    var showCitySelector by remember { mutableStateOf(false) }
    var showRouteSelector by remember { mutableStateOf(false) }
    var showSearchResults by remember { mutableStateOf(false) }
    var showLocationSelection by remember { mutableStateOf(false) }
    var isSelectingOrigin by remember { mutableStateOf(true) }
    var showRouteSearch by remember { mutableStateOf(false) }
    var showRadiusConfig by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }
    var showSaveFavorite by remember { mutableStateOf(false) }
    var showSavedPlacesManager by remember { mutableStateOf(false) }
    var showRouteSelectionForTrip by remember { mutableStateOf(false) }
    var showArrivalModal by remember { mutableStateOf(false) }
    var showProximityConfig by remember { mutableStateOf(false) }
    
    // Permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                     permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (!granted) {
            Toast.makeText(
                context,
                "Permisos de ubicación denegados",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    // Solicitar permisos al inicio
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    // Mostrar resultados de búsqueda cuando hay rutas encontradas
    LaunchedEffect(foundRoutes) {
        if (foundRoutes.isNotEmpty()) {
            showSearchResults = true
        }
    }
    
    // Mostrar errores
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RutasMEX") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Historial de viajes"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón de configuración de proximidad
                if (isTripActive) {
                    SmallFloatingActionButton(
                        onClick = { showProximityConfig = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Configurar proximidad"
                        )
                    }
                }
                
                // Botón de configuración de radios
                if (currentCity != null && !isTripActive) {
                    SmallFloatingActionButton(
                        onClick = { showRadiusConfig = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurar radios"
                        )
                    }
                }
                
                // Botón principal
                ExtendedFloatingActionButton(
                    onClick = {
                        when {
                            currentCity == null -> showCitySelector = true
                            isTripActive -> {
                                // Si hay viaje activo, mostrar modal de llegada
                                showArrivalModal = true
                            }
                            selectedRoutes.isNotEmpty() && origenLocation != null && destinoLocation != null -> {
                                // Si hay rutas seleccionadas, mostrar modal para iniciar viaje
                                showRouteSelectionForTrip = true
                            }
                            else -> showRouteSearch = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = when {
                                currentCity == null -> Icons.Default.Map
                                isTripActive -> Icons.Default.CheckCircle
                                else -> Icons.Default.Search
                            },
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            text = when {
                                currentCity == null -> "Seleccionar Ciudad"
                                isTripActive -> "Finalizar Viaje"
                                selectedRoutes.isNotEmpty() && origenLocation != null && destinoLocation != null -> "Iniciar Viaje"
                                else -> "Buscar Rutas"
                            }
                        )
                    }
                )
            }
        },
        bottomBar = {
            // Modal persistente inferior (siempre visible)
            PersistentBottomSheet(
                routes = availableRoutes,
                selectedRouteIds = selectedRoutes.map { it.id }.toSet(),
                origenLocation = origenLocation,
                destinoLocation = destinoLocation,
                isTripActive = isTripActive,
                hasCitySelected = currentCity != null,
                isFavorite = isFavorite,
                onRouteToggle = { route ->
                    viewModel.toggleRouteSelection(route)
                },
                onOriginTap = {
                    isSelectingOrigin = true
                    showLocationSelection = true
                },
                onDestinationTap = {
                    isSelectingOrigin = false
                    showLocationSelection = true
                },
                onSwap = {
                    viewModel.swapLocations()
                },
                onFavoriteTap = {
                    // Si ya hay origen y destino, mostrar modal para guardar o ver favoritos
                    if (origenLocation != null && destinoLocation != null) {
                        if (isFavorite) {
                            showFavorites = true
                        } else {
                            showSaveFavorite = true
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
            // Mapa
            MapView(
                selectedRoutes = selectedRoutes,
                origenLocation = origenLocation,
                destinoLocation = destinoLocation,
                mapType = mapType,
                onMapClick = { latLng ->
                    // TODO: Implementar geocoding reverso
                    viewModel.handleMapTap(latLng, "Ubicación seleccionada")
                },
                onMapLongClick = { latLng ->
                    // TODO: Mostrar diálogo de opciones
                    viewModel.handleMapTap(latLng, "Ubicación seleccionada")
                }
            )
            
            // Control de viaje activo
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                ActiveTripControl(
                    isTripActive = isTripActive,
                    currentTrip = currentTrip,
                    distanceResult = distanceResult,
                    onStartTrip = { viewModel.startTrip() },
                    onStopTrip = { viewModel.stopTrip() },
                    onCancelTrip = { viewModel.cancelTrip() }
                )
            }
            
            // Chip para cambiar tipo de mapa
            FilterChip(
                selected = mapType == com.azyroapp.rutasmex.ui.viewmodel.MapType.SATELLITE,
                onClick = { viewModel.toggleMapType() },
                label = {
                    Text(
                        text = if (mapType == com.azyroapp.rutasmex.ui.viewmodel.MapType.SATELLITE) {
                            "Normal"
                        } else {
                            "Satélite"
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
            
            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
    
    // Bottom Sheets
    if (showCitySelector) {
        CitySelector(
            cities = cities,
            onCitySelected = { city ->
                viewModel.selectCity(city)
                Toast.makeText(context, "Ciudad: ${city.name}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showCitySelector = false }
        )
    }
    
    if (showRouteSelector && currentCity != null) {
        val tempSelectedRoutes = remember { mutableStateOf(selectedRoutes.toSet()) }
        
        RouteSelector(
            cityName = currentCity!!.name,
            routes = availableRoutes,
            selectedRoutes = tempSelectedRoutes.value,
            onRouteToggle = { route, isSelected ->
                tempSelectedRoutes.value = if (isSelected) {
                    tempSelectedRoutes.value + route
                } else {
                    tempSelectedRoutes.value - route
                }
            },
            onClearAll = {
                tempSelectedRoutes.value = emptySet()
            },
            onApply = {
                // Actualizar ViewModel
                viewModel.clearSelectedRoutes()
                tempSelectedRoutes.value.forEach { route ->
                    viewModel.toggleRouteSelection(route)
                }
                Toast.makeText(
                    context,
                    "${tempSelectedRoutes.value.size} rutas aplicadas",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = { showRouteSelector = false }
        )
    }
    
    if (showSearchResults) {
        SearchResults(
            routes = foundRoutes,
            origen = origenLocation,
            destino = destinoLocation,
            onSelectAll = {
                // Implementar seleccionar todas
            },
            onShowOnMap = { routes ->
                viewModel.clearSelectedRoutes()
                routes.forEach { route ->
                    viewModel.toggleRouteSelection(route)
                }
                Toast.makeText(
                    context,
                    "${routes.size} rutas mostradas en el mapa",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showSearchResults = false
                viewModel.clearSearchResults()
            }
        )
    }
    
    // Modal de selección de ubicación
    if (showLocationSelection) {
        LocationSelectionModal(
            title = if (isSelectingOrigin) "Seleccionar Origen" else "Seleccionar Destino",
            savedPlaces = savedPlaces,
            onPlaceSelected = { place ->
                if (isSelectingOrigin) {
                    viewModel.setOrigen(place)
                } else {
                    viewModel.setDestino(place)
                }
            },
            onUseCurrentLocation = {
                viewModel.useCurrentLocation(isSelectingOrigin)
            },
            onSelectOnMap = {
                if (isSelectingOrigin) {
                    viewModel.startSelectingOrigen()
                } else {
                    viewModel.startSelectingDestino()
                }
            },
            onSearchPlace = { query ->
                viewModel.searchPlace(query)
            },
            onDismiss = {
                showLocationSelection = false
            }
        )
    }
    
    // Modal de búsqueda de rutas
    if (showRouteSearch) {
        val tempSelectedRoutes = remember { mutableStateOf(selectedRoutes.toSet()) }
        
        RouteSearchModal(
            routes = availableRoutes,
            selectedRoutes = tempSelectedRoutes.value,
            onRouteToggle = { route, isSelected ->
                tempSelectedRoutes.value = if (isSelected) {
                    tempSelectedRoutes.value + route
                } else {
                    tempSelectedRoutes.value - route
                }
            },
            onClearAll = {
                tempSelectedRoutes.value = emptySet()
            },
            onApply = {
                viewModel.clearSelectedRoutes()
                tempSelectedRoutes.value.forEach { route ->
                    viewModel.toggleRouteSelection(route)
                }
                Toast.makeText(
                    context,
                    "${tempSelectedRoutes.value.size} rutas aplicadas",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showRouteSearch = false
            }
        )
    }
    
    // Modal de configuración de radios
    if (showRadiusConfig) {
        RadiusConfigModal(
            initialOriginRadius = originRadius,
            initialDestinationRadius = destinationRadius,
            onRadiiChanged = { origin, destination ->
                viewModel.updateSearchRadii(origin, destination)
                Toast.makeText(
                    context,
                    "Radios actualizados",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showRadiusConfig = false
            }
        )
    }
    
    // Modal de favoritos
    if (showFavorites) {
        FavoritesModal(
            favorites = favoriteSearches,
            onFavoriteSelected = { favorite ->
                viewModel.loadFavoriteSearch(favorite)
                Toast.makeText(
                    context,
                    "Favorito cargado: ${favorite.name}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDeleteFavorite = { favorite ->
                viewModel.deleteFavoriteSearch(favorite)
                Toast.makeText(
                    context,
                    "Favorito eliminado",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showFavorites = false
            }
        )
    }
    
    // Modal para guardar favorito
    if (showSaveFavorite) {
        val defaultName = "${origenLocation?.name ?: "Origen"} - ${destinoLocation?.name ?: "Destino"}"
        
        SaveFavoriteModal(
            defaultName = defaultName,
            originName = origenLocation?.name ?: "",
            destinationName = destinoLocation?.name ?: "",
            onSave = { name ->
                viewModel.saveFavoriteSearch(name)
                Toast.makeText(
                    context,
                    "Favorito guardado: $name",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showSaveFavorite = false
            }
        )
    }
    
    // Modal de gestión de lugares guardados
    if (showSavedPlacesManager) {
        SavedPlacesManagerModal(
            places = viewModel.savedPlaces.collectAsState().value,
            onPlaceSelected = { place ->
                // Usar el lugar seleccionado
                val locationPoint = place.toLocationPoint()
                if (isSelectingOrigin) {
                    viewModel.setOrigen(locationPoint)
                } else {
                    viewModel.setDestino(locationPoint)
                }
                viewModel.incrementPlaceUseCount(place.id)
            },
            onDeletePlace = { place ->
                viewModel.deleteSavedPlace(place)
                Toast.makeText(
                    context,
                    "Lugar eliminado",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onEditPlace = { place ->
                // TODO: Implementar edición de lugar
                Toast.makeText(
                    context,
                    "Edición próximamente disponible",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onAddPlace = {
                // TODO: Implementar agregar lugar
                Toast.makeText(
                    context,
                    "Agregar lugar próximamente disponible",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showSavedPlacesManager = false
            }
        )
    }
    
    // Modal de selección de ruta para iniciar viaje
    if (showRouteSelectionForTrip && origenLocation != null && destinoLocation != null) {
        RouteSelectionForTripModal(
            routes = selectedRoutes,
            originName = origenLocation!!.name,
            destinationName = destinoLocation!!.name,
            calculationMode = viewModel.calculationMode.collectAsState().value,
            onStartTripWithRoute = { route ->
                viewModel.setActiveRoute(route)
                viewModel.startTrip()
                showRouteSelectionForTrip = false
                Toast.makeText(
                    context,
                    "Viaje iniciado con ${route.name}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showRouteSelectionForTrip = false
            }
        )
    }
    
    // Modal de llegada al destino
    if (showArrivalModal && currentTrip != null) {
        ArrivalModal(
            trip = currentTrip!!,
            onFinishTrip = {
                viewModel.stopTrip()
                showArrivalModal = false
                Toast.makeText(
                    context,
                    "¡Viaje finalizado exitosamente!",
                    Toast.LENGTH_LONG
                ).show()
            },
            onSaveAsFavorite = {
                if (origenLocation != null && destinoLocation != null) {
                    showSaveFavorite = true
                }
            },
            onShareTrip = {
                currentTrip?.let { trip ->
                    viewModel.shareTrip(trip)
                }
            },
            onDismiss = {
                showArrivalModal = false
            }
        )
    }
    
    // Modal de configuración de proximidad
    if (showProximityConfig) {
        ProximityConfigModal(
            initialProximityDistance = proximityConfig.distance,
            initialNotificationsEnabled = proximityConfig.notificationsEnabled,
            initialSoundEnabled = proximityConfig.soundEnabled,
            initialVibrationEnabled = proximityConfig.vibrationEnabled,
            onConfigChanged = { distance, notifications, sound, vibration ->
                viewModel.updateProximityConfig(distance, notifications, sound, vibration)
                Toast.makeText(
                    context,
                    "Configuración de proximidad guardada",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onDismiss = {
                showProximityConfig = false
            }
        )
    }
}
