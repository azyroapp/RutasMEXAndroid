package com.azyroapp.rutasmex.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
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
    val selectionMode by viewModel.selectionMode.collectAsState()
    val foundRoutes by viewModel.foundRoutes.collectAsState()
    val isTripActive by viewModel.isTripActive.collectAsState()
    val currentTrip by viewModel.currentTrip.collectAsState()
    val distanceResult by viewModel.distanceResult.collectAsState()
    
    // Estados locales para bottom sheets
    var showCitySelector by remember { mutableStateOf(false) }
    var showRouteSelector by remember { mutableStateOf(false) }
    var showSearchResults by remember { mutableStateOf(false) }
    
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
            ExtendedFloatingActionButton(
                onClick = {
                    if (currentCity == null) {
                        showCitySelector = true
                    } else {
                        showRouteSelector = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentCity == null) Icons.Default.Map else Icons.Default.Search,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        text = if (currentCity == null) "Seleccionar Ciudad" else "Seleccionar Rutas"
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                OriginDestinationBar(
                    origen = origenLocation,
                    destino = destinoLocation,
                    canSearch = viewModel.canSearchRoutes(),
                    onSelectOrigen = { viewModel.startSelectingOrigen() },
                    onSelectDestino = { viewModel.startSelectingDestino() },
                    onSwap = { viewModel.swapLocations() },
                    onSearch = { viewModel.searchRoutes() }
                )
            }
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
}
