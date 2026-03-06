package com.azyroapp.rutasmex

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.azyroapp.rutasmex.ui.home.HomeViewModel
import com.azyroapp.rutasmex.ui.map.GeocodingHelper
import com.azyroapp.rutasmex.ui.map.MapHelper
import com.azyroapp.rutasmex.ui.map.MarkerHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    
    private lateinit var map: GoogleMap
    private lateinit var mapHelper: MapHelper
    private lateinit var markerHelper: MarkerHelper
    private lateinit var geocodingHelper: GeocodingHelper
    private val viewModel: HomeViewModel by viewModels()
    
    // Launcher para solicitar permisos de ubicación
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                enableMyLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                enableMyLocation()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Permisos de ubicación denegados",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Obtener el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // Configurar botones
        setupButtons()
        
        // Observar cambios en el ViewModel
        observeViewModel()
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapHelper = MapHelper(map)
        markerHelper = MarkerHelper(map)
        geocodingHelper = GeocodingHelper(this)
        
        // Configuración inicial del mapa
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
            isMapToolbarEnabled = true
        }
        
        // Solicitar permisos de ubicación
        checkLocationPermission()
        
        // Centrar en México (Chiapas - Tuxtla Gutiérrez)
        val tuxtla = LatLng(16.7504034, -93.12392021)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(tuxtla, 12f))
        
        // Configurar listeners del mapa
        setupMapListeners()
    }
    
    private fun setupMapListeners() {
        // Listener para tap en el mapa (para seleccionar origen/destino)
        map.setOnMapClickListener { latLng ->
            val selectionMode = viewModel.selectionMode.value
            
            if (selectionMode != com.azyroapp.rutasmex.ui.home.SelectionMode.NONE) {
                // Realizar geocoding reverso para obtener el nombre del lugar
                performReverseGeocoding(latLng) { locationName ->
                    viewModel.handleMapTap(latLng, locationName)
                }
            }
        }
        
        // Listener para long press (mantener presionado)
        map.setOnMapLongClickListener { latLng ->
            showLocationOptions(latLng)
        }
    }
    
    private fun showLocationOptions(latLng: LatLng) {
        val options = arrayOf(
            "Establecer como Origen",
            "Establecer como Destino",
            "Usar mi ubicación como Origen"
        )
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar acción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        performReverseGeocoding(latLng) { locationName ->
                            viewModel.handleMapTap(latLng, locationName)
                            viewModel.startSelectingOrigen()
                            viewModel.handleMapTap(latLng, locationName)
                        }
                    }
                    1 -> {
                        performReverseGeocoding(latLng) { locationName ->
                            viewModel.handleMapTap(latLng, locationName)
                            viewModel.startSelectingDestino()
                            viewModel.handleMapTap(latLng, locationName)
                        }
                    }
                    2 -> {
                        // Usar ubicación actual como origen
                        // TODO: Implementar cuando tengamos LocationManager
                    }
                }
            }
            .show()
    }
    
    private fun performReverseGeocoding(latLng: LatLng, callback: (String) -> Unit) {
        // Usar coroutine para geocoding asíncrono
        CoroutineScope(Dispatchers.Main).launch {
            val locationName = if (geocodingHelper.isGeocoderAvailable()) {
                geocodingHelper.getLocationName(latLng)
            } else {
                // Fallback si Geocoder no está disponible
                "Lat: ${String.format("%.4f", latLng.latitude)}, Lon: ${String.format("%.4f", latLng.longitude)}"
            }
            callback(locationName)
        }
    }
    
    private fun setupButtons() {
        // Botones del panel superior
        
        // Botón para seleccionar ciudad
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSelectCity)
            .setOnClickListener {
                showCitySelector()
            }
        
        // Botón para buscar rutas
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSearchRoutes)
            .setOnClickListener {
                showRouteSelector()
            }
        
        // Botón para cambiar tipo de mapa
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnMapType)
            .setOnClickListener {
                viewModel.toggleMapType()
            }
        
        // Botones del panel inferior (origen/destino)
        
        // Botón para seleccionar origen
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSelectOrigen)
            .setOnClickListener {
                viewModel.startSelectingOrigen()
            }
        
        // Botón para seleccionar destino
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSelectDestino)
            .setOnClickListener {
                viewModel.startSelectingDestino()
            }
        
        // Botón para intercambiar origen y destino
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSwapLocations)
            .setOnClickListener {
                viewModel.swapLocations()
                Toast.makeText(this, "Ubicaciones intercambiadas", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun showCitySelector() {
        val cities = viewModel.cities.value ?: return
        
        if (cities.isEmpty()) {
            Toast.makeText(this, "Cargando ciudades...", Toast.LENGTH_SHORT).show()
            return
        }
        
        val cityNames = cities.map { it.name }.toTypedArray()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar Ciudad")
            .setItems(cityNames) { _, which ->
                viewModel.selectCity(cities[which])
                Toast.makeText(this, "Ciudad: ${cities[which].name}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
    
    private fun showRouteSelector() {
        val routes = viewModel.availableRoutes.value
        
        if (routes.isNullOrEmpty()) {
            Toast.makeText(this, "Primero selecciona una ciudad", Toast.LENGTH_SHORT).show()
            return
        }
        
        val routeNames = routes.map { it.name }.toTypedArray()
        val selectedRoutes = viewModel.selectedRoutes.value ?: emptyList()
        val checkedItems = routes.map { selectedRoutes.contains(it) }.toBooleanArray()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar Rutas")
            .setMultiChoiceItems(routeNames, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    viewModel.toggleRouteSelection(routes[which])
                } else {
                    viewModel.toggleRouteSelection(routes[which])
                }
            }
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Limpiar") { _, _ ->
                viewModel.clearSelectedRoutes()
            }
            .show()
    }
    
    private fun observeViewModel() {
        // Observar ciudades cargadas
        viewModel.cities.observe(this) { cities ->
            if (cities.isNotEmpty()) {
                // TODO: Mostrar selector de ciudad
            }
        }
        
        // Observar ciudad actual
        viewModel.currentCity.observe(this) { city ->
            city?.let {
                if (::map.isInitialized) {
                    // Centrar mapa en la ciudad
                    val center = LatLng(it.center.latitude, it.center.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))
                }
            }
        }
        
        // Observar rutas disponibles
        viewModel.availableRoutes.observe(this) { routes ->
            // Las rutas están disponibles pero no se dibujan hasta que se seleccionen
        }
        
        // Observar rutas seleccionadas
        viewModel.selectedRoutes.observe(this) { routes ->
            if (routes.isNotEmpty() && ::mapHelper.isInitialized) {
                // Dibujar rutas en el mapa
                mapHelper.drawRoutes(routes, showAllSegments = true)
                
                // Ajustar cámara para mostrar todas las rutas
                mapHelper.calculateBounds(routes)?.let { bounds ->
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            } else if (::mapHelper.isInitialized) {
                // Limpiar rutas del mapa
                mapHelper.clearPolylines()
            }
        }
        
        // Observar tipo de mapa
        viewModel.mapType.observe(this) { mapType ->
            if (::map.isInitialized) {
                map.mapType = when (mapType) {
                    com.azyroapp.rutasmex.ui.home.MapType.NORMAL -> GoogleMap.MAP_TYPE_NORMAL
                    com.azyroapp.rutasmex.ui.home.MapType.SATELLITE -> GoogleMap.MAP_TYPE_SATELLITE
                }
            }
        }
        
        // Observar origen
        viewModel.origenLocation.observe(this) { origen ->
            val tvOrigen = findViewById<android.widget.TextView>(R.id.tvOrigen)
            
            if (origen != null && ::markerHelper.isInitialized) {
                markerHelper.showOrigenMarker(origen)
                tvOrigen.text = origen.name
            } else if (::markerHelper.isInitialized) {
                markerHelper.removeOrigenMarker()
                tvOrigen.text = "Seleccionar origen"
            }
        }
        
        // Observar destino
        viewModel.destinoLocation.observe(this) { destino ->
            val tvDestino = findViewById<android.widget.TextView>(R.id.tvDestino)
            
            if (destino != null && ::markerHelper.isInitialized) {
                markerHelper.showDestinoMarker(destino)
                tvDestino.text = destino.name
            } else if (::markerHelper.isInitialized) {
                markerHelper.removeDestinoMarker()
                tvDestino.text = "Seleccionar destino"
            }
        }
        
        // Observar modo de selección
        viewModel.selectionMode.observe(this) { mode ->
            when (mode) {
                com.azyroapp.rutasmex.ui.home.SelectionMode.SELECTING_ORIGEN -> {
                    Toast.makeText(this, "Toca en el mapa para seleccionar el origen", Toast.LENGTH_SHORT).show()
                }
                com.azyroapp.rutasmex.ui.home.SelectionMode.SELECTING_DESTINO -> {
                    Toast.makeText(this, "Toca en el mapa para seleccionar el destino", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Modo normal
                }
            }
        }
        
        // Observar errores
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            findViewById<android.widget.ProgressBar>(R.id.progressBar).visibility = 
                if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
    
    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
    
    private fun enableMyLocation() {
        try {
            map.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Error al habilitar ubicación: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
