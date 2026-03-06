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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * Genera puntos para dibujar un círculo punteado
 * @param center Centro del círculo
 * @param radius Radio en metros
 * @param segments Número de segmentos (más = más suave)
 * @return Lista de segmentos de línea para crear efecto punteado
 */
private fun generateDashedCircleSegments(
    center: LatLng,
    radius: Double,
    segments: Int = 60
): List<List<LatLng>> {
    val earthRadius = 6371000.0 // Radio de la Tierra en metros
    val segmentsList = mutableListOf<List<LatLng>>()
    
    // Generar puntos del círculo
    val points = mutableListOf<LatLng>()
    for (i in 0..segments) {
        val angle = 2 * PI * i / segments
        
        // Calcular offset en grados
        val latOffset = (radius / earthRadius) * (180 / PI) * cos(angle)
        val lonOffset = (radius / earthRadius) * (180 / PI) * sin(angle) / 
                       cos(center.latitude * PI / 180)
        
        val lat = center.latitude + latOffset
        val lon = center.longitude + lonOffset
        
        points.add(LatLng(lat, lon))
    }
    
    // Crear segmentos punteados (dibujar cada 2 segmentos, saltar 1)
    var i = 0
    while (i < points.size - 1) {
        // Dibujar 2 segmentos
        val dashSegment = mutableListOf<LatLng>()
        for (j in 0..2) {
            if (i + j < points.size) {
                dashSegment.add(points[i + j])
            }
        }
        if (dashSegment.size >= 2) {
            segmentsList.add(dashSegment)
        }
        i += 4 // Saltar para crear el espacio (dash + gap)
    }
    
    return segmentsList
}

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
                // Generar segmentos punteados para cada círculo
                val farSegments = generateDashedCircleSegments(destinoLatLng, proximityFarRadius)
                val mediumSegments = generateDashedCircleSegments(destinoLatLng, proximityMediumRadius)
                val nearSegments = generateDashedCircleSegments(destinoLatLng, proximityNearRadius)
                
                // Círculo Far (rojo claro) - punteado
                farSegments.forEach { segment ->
                    Polyline(
                        points = segment,
                        color = Color(0xFFFF6B6B), // Rojo claro
                        width = 3f
                    )
                }
                
                // Círculo Medium (amarillo) - punteado
                mediumSegments.forEach { segment ->
                    Polyline(
                        points = segment,
                        color = Color(0xFFFFA500), // Amarillo
                        width = 3f
                    )
                }
                
                // Círculo Near (verde) - punteado
                nearSegments.forEach { segment ->
                    Polyline(
                        points = segment,
                        color = Color(0xFF00FF00), // Verde
                        width = 3f
                    )
                }
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
