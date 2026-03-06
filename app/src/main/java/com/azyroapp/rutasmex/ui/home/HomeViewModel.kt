package com.azyroapp.rutasmex.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.azyroapp.rutasmex.data.model.City
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.data.repository.RouteRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

/**
 * ViewModel principal para la pantalla Home
 * Gestiona el estado de la aplicación: ciudades, rutas, ubicaciones, etc.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = RouteRepository(application)
    
    // Estado de ciudades
    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> = _cities
    
    private val _currentCity = MutableLiveData<City?>()
    val currentCity: LiveData<City?> = _currentCity
    
    // Estado de rutas
    private val _availableRoutes = MutableLiveData<List<Route>>()
    val availableRoutes: LiveData<List<Route>> = _availableRoutes
    
    private val _selectedRoutes = MutableLiveData<List<Route>>(emptyList())
    val selectedRoutes: LiveData<List<Route>> = _selectedRoutes
    
    // Estado de ubicaciones
    private val _origenLocation = MutableLiveData<LocationPoint?>()
    val origenLocation: LiveData<LocationPoint?> = _origenLocation
    
    private val _destinoLocation = MutableLiveData<LocationPoint?>()
    val destinoLocation: LiveData<LocationPoint?> = _destinoLocation
    
    // Estado de carga
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // Estado del mapa
    private val _mapType = MutableLiveData<MapType>(MapType.NORMAL)
    val mapType: LiveData<MapType> = _mapType
    
    // Estado del viaje
    private val _isTripActive = MutableLiveData<Boolean>(false)
    val isTripActive: LiveData<Boolean> = _isTripActive
    
    // Modo de selección en el mapa
    private val _selectionMode = MutableLiveData<SelectionMode>(SelectionMode.NONE)
    val selectionMode: LiveData<SelectionMode> = _selectionMode
    
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
        val currentSelected = _selectedRoutes.value ?: emptyList()
        
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
            else -> MapType.NORMAL
        }
    }
    
    /**
     * Inicia un viaje
     */
    fun startTrip() {
        if (_origenLocation.value != null && 
            _destinoLocation.value != null && 
            !_selectedRoutes.value.isNullOrEmpty()) {
            _isTripActive.value = true
        }
    }
    
    /**
     * Detiene el viaje
     */
    fun stopTrip() {
        _isTripActive.value = false
        clearLocations()
        clearSelectedRoutes()
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
    fun handleMapTap(latLng: com.google.android.gms.maps.model.LatLng, locationName: String = "Ubicación seleccionada") {
        when (_selectionMode.value) {
            SelectionMode.SELECTING_ORIGEN -> {
                val location = com.azyroapp.rutasmex.data.model.LocationPoint.fromLatLng(
                    latLng, 
                    locationName
                )
                setOrigen(location)
                _selectionMode.value = SelectionMode.NONE
            }
            SelectionMode.SELECTING_DESTINO -> {
                val location = com.azyroapp.rutasmex.data.model.LocationPoint.fromLatLng(
                    latLng, 
                    locationName
                )
                setDestino(location)
                _selectionMode.value = SelectionMode.NONE
            }
            else -> {
                // No hacer nada si no está en modo de selección
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
