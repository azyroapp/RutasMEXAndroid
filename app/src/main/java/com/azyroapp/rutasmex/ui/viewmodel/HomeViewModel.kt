package com.azyroapp.rutasmex.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azyroapp.rutasmex.core.services.RouteDistanceCalculationService
import com.azyroapp.rutasmex.core.services.TripTrackingHelper
import com.azyroapp.rutasmex.data.model.City
import com.azyroapp.rutasmex.data.model.DistanceCalculationMode
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.data.model.RouteDistanceResult
import com.azyroapp.rutasmex.data.repository.RouteRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel principal para la pantalla Home con Compose
 * Usa StateFlow en lugar de LiveData para mejor integración con Compose
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RouteRepository,
    private val tripDao: com.azyroapp.rutasmex.data.local.TripDao,
    private val preferencesManager: com.azyroapp.rutasmex.data.preferences.PreferencesManager,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {
    
    // Estado de ciudades
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()
    
    private val _currentCity = MutableStateFlow<City?>(null)
    val currentCity: StateFlow<City?> = _currentCity.asStateFlow()
    
    // Estado de rutas
    private val _availableRoutes = MutableStateFlow<List<Route>>(emptyList())
    val availableRoutes: StateFlow<List<Route>> = _availableRoutes.asStateFlow()
    
    private val _selectedRoutes = MutableStateFlow<List<Route>>(emptyList())
    val selectedRoutes: StateFlow<List<Route>> = _selectedRoutes.asStateFlow()
    
    // Estado de ubicaciones
    private val _origenLocation = MutableStateFlow<LocationPoint?>(null)
    val origenLocation: StateFlow<LocationPoint?> = _origenLocation.asStateFlow()
    
    private val _destinoLocation = MutableStateFlow<LocationPoint?>(null)
    val destinoLocation: StateFlow<LocationPoint?> = _destinoLocation.asStateFlow()
    
    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Estado del mapa
    private val _mapType = MutableStateFlow(MapType.NORMAL)
    val mapType: StateFlow<MapType> = _mapType.asStateFlow()
    
    // Modo de selección en el mapa
    private val _selectionMode = MutableStateFlow(SelectionMode.NONE)
    val selectionMode: StateFlow<SelectionMode> = _selectionMode.asStateFlow()
    
    // Rutas encontradas que pasan por origen y destino
    private val _foundRoutes = MutableStateFlow<List<Route>>(emptyList())
    val foundRoutes: StateFlow<List<Route>> = _foundRoutes.asStateFlow()
    
    // Modo de cálculo de distancia
    private val _calculationMode = MutableStateFlow(DistanceCalculationMode.IDA)
    val calculationMode: StateFlow<DistanceCalculationMode> = _calculationMode.asStateFlow()
    
    // Resultado del cálculo de distancia
    private val _distanceResult = MutableStateFlow<RouteDistanceResult?>(null)
    val distanceResult: StateFlow<RouteDistanceResult?> = _distanceResult.asStateFlow()
    
    // Ubicación actual del usuario
    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()
    
    // Ruta activa (para cálculos)
    private val _activeRoute = MutableStateFlow<Route?>(null)
    val activeRoute: StateFlow<Route?> = _activeRoute.asStateFlow()
    
    init {
        loadCities()
        loadPreferences()
    }
    
    /**
     * Carga las preferencias guardadas
     */
    private fun loadPreferences() {
        viewModelScope.launch {
            // Cargar ciudad seleccionada
            preferencesManager.selectedCity.collect { (cityId, cityName) ->
                if (cityId != null && cityName != null) {
                    val city = _cities.value.find { it.id == cityId }
                    if (city != null) {
                        selectCity(city)
                    }
                }
            }
        }
        
        viewModelScope.launch {
            // Cargar modo de cálculo
            preferencesManager.calculationMode.collect { mode ->
                _calculationMode.value = mode
            }
        }
        
        viewModelScope.launch {
            // Cargar tipo de mapa
            preferencesManager.mapType.collect { type ->
                _mapType.value = when (type) {
                    "SATELLITE" -> MapType.SATELLITE
                    else -> MapType.NORMAL
                }
            }
        }
        
        viewModelScope.launch {
            // Cargar origen
            preferencesManager.origin.collect { (lat, lon, name) ->
                if (lat != null && lon != null && name != null) {
                    _origenLocation.value = LocationPoint(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        latitude = lat,
                        longitude = lon
                    )
                }
            }
        }
        
        viewModelScope.launch {
            // Cargar destino
            preferencesManager.destination.collect { (lat, lon, name) ->
                if (lat != null && lon != null && name != null) {
                    _destinoLocation.value = LocationPoint(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name,
                        latitude = lat,
                        longitude = lon
                    )
                }
            }
        }
    }
    
    /**
     * Carga todas las ciudades disponibles
     */
    fun loadCities() {
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.loadCities()
                .onSuccess { cityList ->
                    _cities.value = cityList
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Selecciona una ciudad y carga sus rutas
     */
    fun selectCity(city: City) {
        _currentCity.value = city
        loadRoutesForCurrentCity()
        
        // Guardar en preferencias
        viewModelScope.launch {
            preferencesManager.saveSelectedCity(city.id, city.name)
        }
    }
    
    /**
     * Carga las rutas de la ciudad actual
     */
    private fun loadRoutesForCurrentCity() {
        val city = _currentCity.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            
            repository.loadRoutesForCity(city.id)
                .onSuccess { routeList ->
                    _availableRoutes.value = routeList
                    _isLoading.value = false
                }
                .onFailure { error ->
                    _errorMessage.value = error.message
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Selecciona/deselecciona una ruta
     */
    fun toggleRouteSelection(route: Route) {
        val currentSelected = _selectedRoutes.value
        
        _selectedRoutes.value = if (currentSelected.contains(route)) {
            currentSelected - route
        } else {
            currentSelected + route
        }
    }
    
    /**
     * Limpia todas las rutas seleccionadas
     */
    fun clearSelectedRoutes() {
        _selectedRoutes.value = emptyList()
    }
    
    /**
     * Establece el origen
     */
    fun setOrigen(location: LocationPoint) {
        _origenLocation.value = location
        
        // Guardar en preferencias
        viewModelScope.launch {
            preferencesManager.saveOrigin(location.latitude, location.longitude, location.name)
        }
    }
    
    /**
     * Establece el destino
     */
    fun setDestino(location: LocationPoint) {
        _destinoLocation.value = location
        
        // Guardar en preferencias
        viewModelScope.launch {
            preferencesManager.saveDestination(location.latitude, location.longitude, location.name)
        }
    }
    
    /**
     * Intercambia origen y destino
     */
    fun swapLocations() {
        val tempOrigen = _origenLocation.value
        _origenLocation.value = _destinoLocation.value
        _destinoLocation.value = tempOrigen
    }
    
    /**
     * Limpia origen y destino
     */
    fun clearLocations() {
        _origenLocation.value = null
        _destinoLocation.value = null
        
        // Limpiar en preferencias
        viewModelScope.launch {
            preferencesManager.clearLocations()
        }
    }
    
    /**
     * Cambia el tipo de mapa
     */
    fun toggleMapType() {
        _mapType.value = when (_mapType.value) {
            MapType.NORMAL -> MapType.SATELLITE
            MapType.SATELLITE -> MapType.NORMAL
        }
        
        // Guardar en preferencias
        viewModelScope.launch {
            preferencesManager.saveMapType(_mapType.value.name)
        }
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Inicia el modo de selección de origen
     */
    fun startSelectingOrigen() {
        _selectionMode.value = SelectionMode.SELECTING_ORIGEN
    }
    
    /**
     * Inicia el modo de selección de destino
     */
    fun startSelectingDestino() {
        _selectionMode.value = SelectionMode.SELECTING_DESTINO
    }
    
    /**
     * Cancela el modo de selección
     */
    fun cancelSelection() {
        _selectionMode.value = SelectionMode.NONE
    }
    
    /**
     * Maneja el tap en el mapa según el modo de selección
     */
    fun handleMapTap(latLng: LatLng, locationName: String = "Ubicación seleccionada") {
        when (_selectionMode.value) {
            SelectionMode.SELECTING_ORIGEN -> {
                val location = LocationPoint.fromLatLng(latLng, locationName)
                setOrigen(location)
                _selectionMode.value = SelectionMode.NONE
            }
            SelectionMode.SELECTING_DESTINO -> {
                val location = LocationPoint.fromLatLng(latLng, locationName)
                setDestino(location)
                _selectionMode.value = SelectionMode.NONE
            }
            else -> {
                // No hacer nada si no está en modo de selección
            }
        }
    }
    
    /**
     * Busca rutas que pasan cerca del origen y destino
     */
    fun searchRoutes(searchRadiusMeters: Double = 500.0) {
        val origen = _origenLocation.value
        val destino = _destinoLocation.value
        val routes = _availableRoutes.value
        
        if (origen == null || destino == null) {
            _errorMessage.value = "Debes seleccionar origen y destino"
            return
        }
        
        if (routes.isEmpty()) {
            _errorMessage.value = "No hay rutas disponibles. Selecciona una ciudad primero"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val foundRoutesList = routes.filter { route ->
                    val passesNearOrigen = route.passesNearPoint(
                        origen.latitude, 
                        origen.longitude, 
                        searchRadiusMeters
                    )
                    val passesNearDestino = route.passesNearPoint(
                        destino.latitude, 
                        destino.longitude, 
                        searchRadiusMeters
                    )
                    
                    passesNearOrigen && passesNearDestino
                }
                
                _foundRoutes.value = foundRoutesList
                _isLoading.value = false
                
                if (foundRoutesList.isEmpty()) {
                    _errorMessage.value = "No se encontraron rutas que pasen por ambos puntos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar rutas: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpia los resultados de búsqueda
     */
    fun clearSearchResults() {
        _foundRoutes.value = emptyList()
    }
    
    /**
     * Verifica si se puede buscar rutas
     */
    fun canSearchRoutes(): Boolean {
        return _origenLocation.value != null && 
               _destinoLocation.value != null && 
               _currentCity.value != null
    }
    
    /**
     * Actualiza la ubicación del usuario
     */
    fun updateUserLocation(location: Location) {
        _userLocation.value = location
        
        // Si hay origen, destino y ruta activa, calcular distancia
        if (_origenLocation.value != null && 
            _destinoLocation.value != null && 
            _activeRoute.value != null) {
            calculateDistance()
        }
    }
    
    /**
     * Establece la ruta activa para cálculos
     */
    fun setActiveRoute(route: Route?) {
        _activeRoute.value = route
        
        // Guardar en preferencias
        if (route != null) {
            viewModelScope.launch {
                preferencesManager.saveLastActiveRoute(route.id)
            }
        }
        
        // Si hay origen, destino y ubicación, calcular distancia
        if (route != null && 
            _origenLocation.value != null && 
            _destinoLocation.value != null && 
            _userLocation.value != null) {
            calculateDistance()
        }
    }
    
    /**
     * Cambia el modo de cálculo de distancia
     */
    fun toggleCalculationMode() {
        _calculationMode.value = _calculationMode.value.next()
        
        // Guardar en preferencias
        viewModelScope.launch {
            preferencesManager.saveCalculationMode(_calculationMode.value, isManual = true)
        }
        
        // Recalcular con el nuevo modo
        if (_origenLocation.value != null && 
            _destinoLocation.value != null && 
            _activeRoute.value != null && 
            _userLocation.value != null) {
            calculateDistance()
        }
    }
    
    /**
     * Calcula la distancia usando el servicio de cálculo de rutas
     */
    private fun calculateDistance() {
        val origen = _origenLocation.value ?: return
        val destino = _destinoLocation.value ?: return
        val route = _activeRoute.value ?: return
        val userLoc = _userLocation.value ?: return
        
        viewModelScope.launch {
            try {
                // Convertir LocationPoint a Location
                val originLocation = Location("").apply {
                    latitude = origen.latitude
                    longitude = origen.longitude
                }
                
                val destinationLocation = Location("").apply {
                    latitude = destino.latitude
                    longitude = destino.longitude
                }
                
                // Calcular distancia
                val result = RouteDistanceCalculationService.calculateDistanceAlongRoute(
                    userLocation = userLoc,
                    origin = originLocation,
                    destination = destinationLocation,
                    route = route,
                    calculationMode = _calculationMode.value
                )
                
                if (result != null) {
                    _distanceResult.value = result
                    
                    // Si el modo fue auto-seleccionado, actualizar el modo actual
                    if (result.selectedMode != _calculationMode.value) {
                        _calculationMode.value = result.selectedMode
                    }
                } else {
                    _errorMessage.value = "No se pudo calcular la distancia"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al calcular distancia: ${e.message}"
            }
        }
    }
    
    // ========== TRIP TRACKING ==========
    
    // Estado del viaje activo
    private val _isTripActive = MutableStateFlow(false)
    val isTripActive: StateFlow<Boolean> = _isTripActive.asStateFlow()
    
    private val _currentTrip = MutableStateFlow<com.azyroapp.rutasmex.data.model.Trip?>(null)
    val currentTrip: StateFlow<com.azyroapp.rutasmex.data.model.Trip?> = _currentTrip.asStateFlow()
    
    // Historial de viajes
    val tripHistory = tripDao.getRecentTrips(20)
    
    /**
     * Inicia un viaje
     */
    fun startTrip() {
        val origen = _origenLocation.value
        val destino = _destinoLocation.value
        val route = _activeRoute.value
        val city = _currentCity.value
        val result = _distanceResult.value
        
        if (origen == null || destino == null || route == null || city == null || result == null) {
            _errorMessage.value = "Faltan datos para iniciar el viaje"
            return
        }
        
        viewModelScope.launch {
            try {
                val trip = com.azyroapp.rutasmex.data.model.Trip(
                    cityId = city.id,
                    cityName = city.name,
                    routeId = route.id,
                    routeName = route.name,
                    originLatitude = origen.latitude,
                    originLongitude = origen.longitude,
                    originName = origen.name,
                    destinationLatitude = destino.latitude,
                    destinationLongitude = destino.longitude,
                    destinationName = destino.name,
                    startTime = java.util.Date(),
                    totalDistance = result.totalDistance,
                    calculationMode = _calculationMode.value.name
                )
                
                // Guardar en base de datos
                tripDao.insertTrip(trip)
                
                // Actualizar estado
                _currentTrip.value = trip
                _isTripActive.value = true
                
                // Iniciar TripTrackingService
                TripTrackingHelper.startTripTracking(
                    context = context,
                    trip = trip,
                    route = route,
                    calculationMode = _calculationMode.value
                )
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al iniciar viaje: ${e.message}"
            }
        }
    }
    
    /**
     * Detiene el viaje actual
     */
    fun stopTrip() {
        val trip = _currentTrip.value ?: return
        
        viewModelScope.launch {
            try {
                val endTime = java.util.Date()
                val duration = (endTime.time - trip.startTime.time) / 1000 // segundos
                
                val updatedTrip = trip.copy(
                    endTime = endTime,
                    isCompleted = true,
                    duration = duration
                )
                
                tripDao.updateTrip(updatedTrip)
                
                // Limpiar estado
                _currentTrip.value = null
                _isTripActive.value = false
                
                // Detener TripTrackingService
                TripTrackingHelper.stopTripTracking(context)
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al detener viaje: ${e.message}"
            }
        }
    }
    
    /**
     * Cancela el viaje actual
     */
    fun cancelTrip() {
        val trip = _currentTrip.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedTrip = trip.copy(
                    endTime = java.util.Date(),
                    isCancelled = true
                )
                
                tripDao.updateTrip(updatedTrip)
                
                // Limpiar estado
                _currentTrip.value = null
                _isTripActive.value = false
                
                // Cancelar TripTrackingService
                TripTrackingHelper.cancelTripTracking(context)
                
            } catch (e: Exception) {
                _errorMessage.value = "Error al cancelar viaje: ${e.message}"
            }
        }
    }
    
    /**
     * Elimina un viaje del historial
     */
    fun deleteTrip(trip: com.azyroapp.rutasmex.data.model.Trip) {
        viewModelScope.launch {
            try {
                tripDao.deleteTrip(trip)
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar viaje: ${e.message}"
            }
        }
    }
    
    /**
     * Limpia todo el historial de viajes
     */
    fun clearTripHistory() {
        viewModelScope.launch {
            try {
                tripDao.deleteAllTrips()
            } catch (e: Exception) {
                _errorMessage.value = "Error al limpiar historial: ${e.message}"
            }
        }
    }
}

/**
 * Tipos de mapa
 */
enum class MapType {
    NORMAL,
    SATELLITE
}

/**
 * Modos de selección en el mapa
 */
enum class SelectionMode {
    NONE,
    SELECTING_ORIGEN,
    SELECTING_DESTINO
}
