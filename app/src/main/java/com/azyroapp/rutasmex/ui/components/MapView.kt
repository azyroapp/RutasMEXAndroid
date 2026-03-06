package com.azyroapp.rutasmex.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.ui.theme.MarkerDestino
import com.azyroapp.rutasmex.ui.theme.MarkerOrigen
import com.azyroapp.rutasmex.ui.theme.RouteCompleto
import com.azyroapp.rutasmex.ui.theme.RouteIda
import com.azyroapp.rutasmex.ui.theme.RouteRegreso
import com.azyroapp.rutasmex.ui.viewmodel.MapType
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Componente de mapa con Google Maps Compose
 * Muestra rutas, marcadores, círculos de radios y proximidad
 */
@Composable
fun MapView(
    selectedRoutes: List<Route>,
    origenLocation: LocationPoint?,
    destinoLocation: LocationPoint?,
    mapType: MapType,
    originRadius: Double = 200.0,
    destinationRadius: Double = 200.0,
    proximityFarRadius: Double = 500.0,
    proximityMediumRadius: Double = 300.0,
    proximityNearRadius: Double = 100.0,
    showProximityCircles: Boolean = false,
    onMapClick: (LatLng) -> Unit,
    onMapLongClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier,
    isLocationPermissionGranted: Boolean = false
) {
    // Estado del mapa
    val tuxtla = LatLng(16.7504034, -93.12392021)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tuxtla, 12f)
    }
    
    // Propiedades del mapa
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = true,
                mapToolbarEnabled = false
            )
        )
    }
    
    val properties by remember(mapType, isLocationPermissionGranted) {
        mutableStateOf(
            MapProperties(
                mapType = when (mapType) {
                    MapType.NORMAL -> com.google.maps.android.compose.MapType.NORMAL
                    MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
                },
                isMyLocationEnabled = isLocationPermissionGranted
            )
        )
    }
    
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = onMapClick,
        onMapLongClick = onMapLongClick
    ) {
        // Dibujar rutas seleccionadas
        selectedRoutes.forEach { route ->
            route.coordinates.forEach { segment ->
                val coordinates = segment.toLatLngList()
                
                // Determinar color del segmento
                val color = when {
                    segment.name.contains("IDA", ignoreCase = true) -> RouteIda
                    segment.name.contains("REGRESO", ignoreCase = true) -> RouteRegreso
                    else -> RouteCompleto
                }
                
                Polyline(
                    points = coordinates,
                    color = color,
                    width = 8f
                )
            }
        }
        
        // Círculos de radios de origen
        origenLocation?.let { origen ->
            val origenLatLng = LatLng(origen.latitude, origen.longitude)
            
            // Círculo de radio de búsqueda (verde semi-transparente)
            Circle(
                center = origenLatLng,
                radius = originRadius,
                fillColor = Color(0x2000FF00), // Verde 12% opacidad
                strokeColor = Color(0xFF00FF00), // Verde sólido
                strokeWidth = 2f
            )
            
            // Marcador de origen
            Marker(
                state = MarkerState(position = origenLatLng),
                title = "Origen",
                snippet = origen.name,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }
        
        // Círculos de radios de destino y proximidad
        destinoLocation?.let { destino ->
            val destinoLatLng = LatLng(destino.latitude, destino.longitude)
            
            // Círculo de radio de búsqueda (rojo semi-transparente)
            Circle(
                center = destinoLatLng,
                radius = destinationRadius,
                fillColor = Color(0x20FF0000), // Rojo 12% opacidad
                strokeColor = Color(0xFFFF0000), // Rojo sólido
                strokeWidth = 2f
            )
            
            // Círculos de proximidad (solo si está habilitado)
            if (showProximityCircles) {
                // Círculo Far (rojo claro)
                Circle(
                    center = destinoLatLng,
                    radius = proximityFarRadius,
                    fillColor = Color(0x10FF6B6B), // Rojo claro 6% opacidad
                    strokeColor = Color(0xFFFF6B6B), // Rojo claro
                    strokeWidth = 3f
                )
                
                // Círculo Medium (amarillo)
                Circle(
                    center = destinoLatLng,
                    radius = proximityMediumRadius,
                    fillColor = Color(0x10FFA500), // Amarillo 6% opacidad
                    strokeColor = Color(0xFFFFA500), // Amarillo
                    strokeWidth = 3f
                )
                
                // Círculo Near (verde)
                Circle(
                    center = destinoLatLng,
                    radius = proximityNearRadius,
                    fillColor = Color(0x1000FF00), // Verde 6% opacidad
                    strokeColor = Color(0xFF00FF00), // Verde
                    strokeWidth = 3f
                )
            }
            
            // Marcador de destino
            Marker(
                state = MarkerState(position = destinoLatLng),
                title = "Destino",
                snippet = destino.name,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }
    }
}
