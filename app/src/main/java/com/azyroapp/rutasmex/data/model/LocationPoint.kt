package com.azyroapp.rutasmex.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * Punto de ubicación (origen o destino)
 */
data class LocationPoint(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
) {
    /**
     * Convierte a LatLng de Google Maps
     */
    fun toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
    
    companion object {
        /**
         * Crea un LocationPoint desde LatLng
         */
        fun fromLatLng(latLng: LatLng, name: String, id: String = java.util.UUID.randomUUID().toString()): LocationPoint {
            return LocationPoint(
                id = id,
                name = name,
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )
        }
    }
}
