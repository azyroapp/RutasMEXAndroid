package com.azyroapp.rutasmex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azyroapp.rutasmex.data.model.City
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.data.repository.RouteRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val repository: RouteRepository
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
    
    init {
        loadCities()
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
    }
    
    /**
     * Establece el destino
     */
    fun setDestino(location: LocationPoint) {
        _destinoLocation.value = location
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
    }
    
    /**
     * Cambia el tipo de mapa
     */
    fun toggleMapType() {
        _mapType.value = when (_mapType.value) {
            MapType.NORMAL -> MapType.SATELLITE
            MapType.SATELLITE -> MapType.NORMAL
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
