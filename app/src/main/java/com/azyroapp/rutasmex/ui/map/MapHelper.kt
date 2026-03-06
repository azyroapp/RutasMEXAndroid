package com.azyroapp.rutasmex.ui.map

import android.graphics.Color
import com.azyroapp.rutasmex.data.model.Route
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.Polyline

/**
 * Helper para gestionar elementos del mapa (polylines, markers, etc.)
 */
class MapHelper(private val map: GoogleMap) {
    
    private val drawnPolylines = mutableListOf<Polyline>()
    
    /**
     * Dibuja una ruta en el mapa
     */
    fun drawRoute(route: Route, showAllSegments: Boolean = true) {
        if (showAllSegments && route.hasMultipleSegments()) {
            // Dibujar todos los segmentos con sus colores
            route.getSegmentCoordinates().forEach { (name, coordinates) ->
                val color = getColorForSegment(name, route)
                drawPolyline(coordinates, color, route.name)
            }
        } else {
            // Dibujar solo el primer segmento
            val coordinates = route.getPrimaryCoordinates()
            val color = parseColor(route.getPrimaryColor()) ?: Color.BLUE
            drawPolyline(coordinates, color, route.name)
        }
    }
    
    /**
     * Dibuja múltiples rutas en el mapa
     */
    fun drawRoutes(routes: List<Route>, showAllSegments: Boolean = true) {
        clearPolylines()
        routes.forEach { route ->
            drawRoute(route, showAllSegments)
        }
    }
    
    /**
     * Dibuja una polyline en el mapa
     */
    private fun drawPolyline(
        coordinates: List<LatLng>,
        color: Int,
        title: String? = null
    ): Polyline? {
        if (coordinates.isEmpty()) return null
        
        val polylineOptions = PolylineOptions()
            .addAll(coordinates)
            .color(color)
            .width(10f)
            .geodesic(true)
        
        val polyline = map.addPolyline(polylineOptions)
        polyline.tag = title
        drawnPolylines.add(polyline)
        
        return polyline
    }
    
    /**
     * Limpia todas las polylines del mapa
     */
    fun clearPolylines() {
        drawnPolylines.forEach { it.remove() }
        drawnPolylines.clear()
    }
    
    /**
     * Obtiene el color para un segmento específico
     */
    private fun getColorForSegment(segmentName: String, route: Route): Int {
        // Buscar el color del segmento en los datos de la ruta
        val segment = route.coordinates.find { it.name == segmentName }
        val colorString = segment?.color
        
        return if (colorString != null) {
            parseColor(colorString) ?: getDefaultColorForSegment(segmentName)
        } else {
            getDefaultColorForSegment(segmentName)
        }
    }
    
    /**
     * Obtiene el color por defecto según el nombre del segmento
     */
    private fun getDefaultColorForSegment(segmentName: String): Int {
        return when {
            segmentName.contains("IDA", ignoreCase = true) -> Color.parseColor("#00C3FF") // Azul
            segmentName.contains("REGRESO", ignoreCase = true) -> Color.parseColor("#FF6B00") // Naranja
            segmentName.contains("COMPLETO", ignoreCase = true) -> Color.parseColor("#9C27B0") // Morado
            else -> Color.BLUE
        }
    }
    
    /**
     * Parsea un string de color hexadecimal a Int
     */
    private fun parseColor(colorString: String?): Int? {
        if (colorString == null) return null
        
        return try {
            Color.parseColor(colorString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    /**
     * Calcula los bounds de una lista de rutas
     */
    fun calculateBounds(routes: List<Route>): LatLngBounds? {
        if (routes.isEmpty()) return null
        
        val builder = LatLngBounds.Builder()
        var hasPoints = false
        
        routes.forEach { route ->
            route.getAllCoordinates().forEach { latLng ->
                builder.include(latLng)
                hasPoints = true
            }
        }
        
        return if (hasPoints) builder.build() else null
    }
    
    /**
     * Calcula los bounds de una sola ruta
     */
    fun calculateBounds(route: Route): LatLngBounds? {
        val coordinates = route.getAllCoordinates()
        if (coordinates.isEmpty()) return null
        
        val builder = LatLngBounds.Builder()
        coordinates.forEach { builder.include(it) }
        
        return builder.build()
    }
}
