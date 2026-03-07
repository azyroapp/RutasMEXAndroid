package com.azyroapp.rutasmex.core.services

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Servicio para geocoding y búsqueda de lugares
 * Usa Geocoder de Android (no requiere API key)
 */
object GeocodingService {
    
    /**
     * Geocoding reverso: convierte coordenadas a nombre de lugar
     */
    suspend fun reverseGeocode(
        context: Context,
        latitude: Double,
        longitude: Double
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("GeocodingService", "🌍 Iniciando reverseGeocode para: $latitude, $longitude")
            
            if (!Geocoder.isPresent()) {
                android.util.Log.e("GeocodingService", "❌ Geocoder no disponible en este dispositivo")
                return@withContext Result.failure(
                    Exception("Geocoder no disponible en este dispositivo")
                )
            }
            
            android.util.Log.d("GeocodingService", "✅ Geocoder disponible, API Level: ${Build.VERSION.SDK_INT}")
            val geocoder = Geocoder(context, Locale.getDefault())
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+ - Usar callback asíncrono
                android.util.Log.d("GeocodingService", "📱 Usando API 33+ (callback asíncrono)")
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1
                    ) { addresses ->
                        android.util.Log.d("GeocodingService", "📍 Callback recibido, addresses.size: ${addresses.size}")
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val placeName = formatAddress(address)
                            android.util.Log.d("GeocodingService", "✅ Dirección encontrada: $placeName")
                            continuation.resume(Result.success(placeName))
                        } else {
                            val fallback = "Lat: ${String.format("%.4f", latitude)}, Lon: ${String.format("%.4f", longitude)}"
                            android.util.Log.w("GeocodingService", "⚠️ No se encontró dirección, usando fallback: $fallback")
                            continuation.resume(Result.success(fallback))
                        }
                    }
                }
            } else {
                // API < 33 - Usar método síncrono
                android.util.Log.d("GeocodingService", "📱 Usando API < 33 (método síncrono)")
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                
                android.util.Log.d("GeocodingService", "📍 Resultado recibido, addresses: ${addresses?.size ?: 0}")
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val placeName = formatAddress(address)
                    android.util.Log.d("GeocodingService", "✅ Dirección encontrada: $placeName")
                    Result.success(placeName)
                } else {
                    val fallback = "Lat: ${String.format("%.4f", latitude)}, Lon: ${String.format("%.4f", longitude)}"
                    android.util.Log.w("GeocodingService", "⚠️ No se encontró dirección, usando fallback: $fallback")
                    Result.success(fallback)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("GeocodingService", "❌ Error en reverseGeocode: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Geocoding: convierte nombre de lugar a coordenadas
     */
    suspend fun geocode(
        context: Context,
        locationName: String,
        maxResults: Int = 5
    ): Result<List<Address>> = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) {
                return@withContext Result.failure(
                    Exception("Geocoder no disponible en este dispositivo")
                )
            }
            
            val geocoder = Geocoder(context, Locale.getDefault())
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+ - Usar callback asíncrono
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(
                        locationName,
                        maxResults
                    ) { addresses ->
                        continuation.resume(Result.success(addresses))
                    }
                }
            } else {
                // API < 33 - Usar método síncrono
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(locationName, maxResults)
                Result.success(addresses ?: emptyList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Busca lugares por nombre con bias hacia Chiapas
     */
    suspend fun searchPlaces(
        context: Context,
        query: String,
        biasLatitude: Double = 16.7504034, // Tuxtla Gutiérrez
        biasLongitude: Double = -93.12392021,
        maxResults: Int = 10
    ): Result<List<SearchResult>> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                return@withContext Result.success(emptyList())
            }
            
            // Buscar con el query original
            val queryWithBias = "$query, Chiapas, México"
            val result = geocode(context, queryWithBias, maxResults)
            
            result.fold(
                onSuccess = { addresses ->
                    val searchResults = addresses.map { address ->
                        SearchResult(
                            name = formatAddress(address),
                            address = formatFullAddress(address),
                            latitude = address.latitude,
                            longitude = address.longitude,
                            distance = calculateDistance(
                                biasLatitude,
                                biasLongitude,
                                address.latitude,
                                address.longitude
                            )
                        )
                    }.sortedBy { it.distance } // Ordenar por distancia
                    
                    Result.success(searchResults)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Formatea una dirección de forma legible
     */
    private fun formatAddress(address: Address): String {
        return buildString {
            // Prioridad: nombre del lugar > dirección > coordenadas
            when {
                !address.featureName.isNullOrBlank() && address.featureName != address.thoroughfare -> {
                    append(address.featureName)
                }
                !address.thoroughfare.isNullOrBlank() -> {
                    append(address.thoroughfare)
                    if (!address.subThoroughfare.isNullOrBlank()) {
                        append(" ${address.subThoroughfare}")
                    }
                }
                !address.locality.isNullOrBlank() -> {
                    append(address.locality)
                }
                else -> {
                    append("Lat: ${String.format("%.4f", address.latitude)}, ")
                    append("Lon: ${String.format("%.4f", address.longitude)}")
                }
            }
        }
    }
    
    /**
     * Formatea la dirección completa
     */
    private fun formatFullAddress(address: Address): String {
        return buildString {
            if (!address.thoroughfare.isNullOrBlank()) {
                append(address.thoroughfare)
                if (!address.subThoroughfare.isNullOrBlank()) {
                    append(" ${address.subThoroughfare}")
                }
                append(", ")
            }
            if (!address.locality.isNullOrBlank()) {
                append(address.locality)
                append(", ")
            }
            if (!address.adminArea.isNullOrBlank()) {
                append(address.adminArea)
            }
        }.trim().trimEnd(',')
    }
    
    /**
     * Calcula distancia entre dos puntos (en km)
     */
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371.0 // km
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }
}

/**
 * Resultado de búsqueda de lugar
 */
data class SearchResult(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double // Distancia desde el punto de bias (km)
)
