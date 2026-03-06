package com.azyroapp.rutasmex.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Helper para realizar geocoding reverso (coordenadas -> dirección)
 */
class GeocodingHelper(private val context: Context) {
    
    private val geocoder = Geocoder(context, Locale("es", "MX"))
    
    /**
     * Obtiene el nombre de una ubicación a partir de coordenadas
     * Usa la API moderna de Geocoder si está disponible (Android 13+)
     */
    suspend fun getLocationName(latLng: LatLng): String = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API moderna (Android 13+)
                getLocationNameModern(latLng)
            } else {
                // API legacy
                getLocationNameLegacy(latLng)
            }
        } catch (e: Exception) {
            // Si falla el geocoding, retornar coordenadas formateadas
            formatCoordinates(latLng)
        }
    }
    
    /**
     * API moderna de Geocoder (Android 13+)
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getLocationNameModern(latLng: LatLng): String = 
        withContext(Dispatchers.IO) {
            var result = formatCoordinates(latLng)
            
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            ) { addresses ->
                if (addresses.isNotEmpty()) {
                    result = formatAddress(addresses[0])
                }
            }
            
            // Esperar un poco para que el callback se ejecute
            kotlinx.coroutines.delay(500)
            result
        }
    
    /**
     * API legacy de Geocoder (Android < 13)
     */
    @Suppress("DEPRECATION")
    private fun getLocationNameLegacy(latLng: LatLng): String {
        return try {
            val addresses = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
            
            if (!addresses.isNullOrEmpty()) {
                formatAddress(addresses[0])
            } else {
                formatCoordinates(latLng)
            }
        } catch (e: Exception) {
            formatCoordinates(latLng)
        }
    }
    
    /**
     * Formatea una dirección de manera legible
     * Prioridad: nombre de lugar > calle > colonia > ciudad
     */
    private fun formatAddress(address: Address): String {
        return buildString {
            // Nombre del lugar (ej: "Parque Central")
            address.featureName?.let { feature ->
                if (feature.isNotBlank() && !feature.matches(Regex("\\d+"))) {
                    append(feature)
                    return@buildString
                }
            }
            
            // Dirección de calle
            address.thoroughfare?.let { street ->
                append(street)
                address.subThoroughfare?.let { number ->
                    append(" $number")
                }
            }
            
            // Si no hay calle, usar colonia o localidad
            if (isEmpty()) {
                address.subLocality?.let { append(it) }
                    ?: address.locality?.let { append(it) }
            }
            
            // Agregar ciudad si es diferente
            if (isNotEmpty()) {
                address.locality?.let { city ->
                    if (!contains(city)) {
                        append(", $city")
                    }
                }
            }
            
            // Si todo falla, usar coordenadas
            if (isEmpty()) {
                append(formatCoordinates(LatLng(address.latitude, address.longitude)))
            }
        }
    }
    
    /**
     * Formatea coordenadas de manera legible
     */
    private fun formatCoordinates(latLng: LatLng): String {
        return "Lat: ${String.format("%.4f", latLng.latitude)}, " +
               "Lon: ${String.format("%.4f", latLng.longitude)}"
    }
    
    /**
     * Verifica si el servicio de geocoding está disponible
     */
    fun isGeocoderAvailable(): Boolean {
        return Geocoder.isPresent()
    }
}
