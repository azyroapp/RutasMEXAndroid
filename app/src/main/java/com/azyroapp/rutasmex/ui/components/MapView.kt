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
 */
@Composable
fun MapView(
    selectedRoutes: List<Route>,
    origenLocation: LocationPoint?,
    destinoLocation: LocationPoint?,
    mapType: MapType,
    onMapClick: (LatLng) -> Unit,
    onMapLongClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
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
    
    val properties by remember(mapType) {
        mutableStateOf(
            MapProperties(
                mapType = when (mapType) {
                    MapType.NORMAL -> com.google.maps.android.compose.MapType.NORMAL
                    MapType.SATELLITE -> com.google.maps.android.compose.MapType.SATELLITE
                },
                isMyLocationEnabled = false // Se habilitará con permisos
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
        
        // Marcador de origen
        origenLocation?.let { origen ->
            Marker(
                state = MarkerState(position = LatLng(origen.latitude, origen.longitude)),
                title = "Origen",
                snippet = origen.name,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
            )
        }
        
        // Marcador de destino
        destinoLocation?.let { destino ->
            Marker(
                state = MarkerState(position = LatLng(destino.latitude, destino.longitude)),
                title = "Destino",
                snippet = destino.name,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )
        }
    }
}
