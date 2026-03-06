package com.azyroapp.rutasmex.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

/**
 * Segmento de ruta (IDA/REGRESO)
 */
data class RouteSegment(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("color")
    val color: String?,
    
    @SerializedName("coordinates")
    val coordinates: List<List<Double>> // [longitude, latitude]
) {
    /**
     * Convierte las coordenadas a LatLng de Google Maps
     * IMPORTANTE: Las coordenadas vienen como [lon, lat], pero LatLng usa (lat, lon)
     */
    fun toLatLngList(): List<LatLng> {
        return coordinates.map { coord ->
            LatLng(coord[1], coord[0]) // lat = coord[1], lon = coord[0]
        }
    }
}

/**
 * Tipo de ruta
 */
enum class RouteType {
    @SerializedName("MultiLineString")
    MULTI_LINE_STRING,
    
    @SerializedName("Point")
    POINT
}

/**
 * Modelo de datos para una ruta de transporte
 */
data class Route(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("type")
    val type: RouteType,
    
    @SerializedName("coordinates")
    val coordinates: List<RouteSegment>,
    
    @SerializedName("centroide")
    val centroide: GeoPoint,
    
    @SerializedName("totalPoints")
    val totalPoints: Int,
    
    @SerializedName("bounds")
    val bounds: GeoBounds,
    
    @SerializedName("routeType")
    val routeType: String,
    
    @SerializedName("itinerary")
    val itinerary: String
) {
    /**
     * Obtiene todas las coordenadas de todos los segmentos combinadas
     */
    fun getAllCoordinates(): List<LatLng> {
        return coordinates.flatMap { it.toLatLngList() }
    }
    
    /**
     * Obtiene las coordenadas del primer segmento (IDA)
     */
    fun getPrimaryCoordinates(): List<LatLng> {
        return coordinates.firstOrNull()?.toLatLngList() ?: emptyList()
    }
    
    /**
     * Obtiene las coordenadas por segmento
     */
    fun getSegmentCoordinates(): List<Pair<String, List<LatLng>>> {
        return coordinates.map { segment ->
            segment.name to segment.toLatLngList()
        }
    }
    
    /**
     * Verifica si tiene múltiples segmentos (IDA y REGRESO)
     */
    fun hasMultipleSegments(): Boolean {
        return coordinates.size > 1
    }
    
    /**
     * Obtiene el color del primer segmento
     */
    fun getPrimaryColor(): String? {
        return coordinates.firstOrNull()?.color
    }
    
    /**
     * Nombre corto para mostrar (solo número si aplica)
     * Ejemplo: "Ruta 23" -> "23"
     */
    fun getDisplayName(): String {
        val pattern = "^Ruta\\s+(\\d+)$".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = pattern.find(name)
        return matchResult?.groupValues?.getOrNull(1) ?: name
    }
    
    /**
     * Verifica si es una ruta numérica (formato "Ruta XX")
     */
    fun isNumericRoute(): Boolean {
        return getDisplayName() != name
    }
    
    /**
     * Verifica si es una ruta foránea
     */
    fun isForaneaRoute(): Boolean {
        return routeType.contains("foranea", ignoreCase = true)
    }
}
