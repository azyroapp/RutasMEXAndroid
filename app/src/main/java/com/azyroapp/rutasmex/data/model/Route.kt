package com.azyroapp.rutasmex.data.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Segmento de ruta (IDA/REGRESO)
 */
@Parcelize
data class RouteSegment(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("color")
    val color: String?,
    
    @SerializedName("coordinates")
    val coordinates: List<List<Double>> // [longitude, latitude]
) : Parcelable {
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
@Parcelize
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
) : Parcelable {
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
    
    /**
     * Verifica si la ruta pasa cerca de un punto dado
     * @param latitude Latitud del punto
     * @param longitude Longitud del punto
     * @param radiusMeters Radio de búsqueda en metros (por defecto 500m)
     * @return true si algún punto de la ruta está dentro del radio
     */
    fun passesNearPoint(latitude: Double, longitude: Double, radiusMeters: Double = 500.0): Boolean {
        val allCoords = getAllCoordinates()
        
        return allCoords.any { coord ->
            val distance = calculateDistance(
                latitude, longitude,
                coord.latitude, coord.longitude
            )
            distance <= radiusMeters
        }
    }
    
    /**
     * Calcula la distancia en metros entre dos puntos usando la fórmula de Haversine
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // Radio de la Tierra en metros
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
}
