package com.azyroapp.rutasmex.ui.map

import android.graphics.Color
import com.azyroapp.rutasmex.data.model.LocationPoint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Helper para gestionar marcadores en el mapa
 */
class MarkerHelper(private val map: GoogleMap) {
    
    private var origenMarker: Marker? = null
    private var destinoMarker: Marker? = null
    private var userMarker: Marker? = null
    
    /**
     * Muestra el marcador de origen (verde)
     */
    fun showOrigenMarker(location: LocationPoint) {
        // Remover marcador anterior si existe
        origenMarker?.remove()
        
        // Crear nuevo marcador
        val markerOptions = MarkerOptions()
            .position(location.toLatLng())
            .title("Origen")
            .snippet(location.name)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        
        origenMarker = map.addMarker(markerOptions)
        origenMarker?.showInfoWindow()
    }
    
    /**
     * Muestra el marcador de destino (rojo)
     */
    fun showDestinoMarker(location: LocationPoint) {
        // Remover marcador anterior si existe
        destinoMarker?.remove()
        
        // Crear nuevo marcador
        val markerOptions = MarkerOptions()
            .position(location.toLatLng())
            .title("Destino")
            .snippet(location.name)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        
        destinoMarker = map.addMarker(markerOptions)
        destinoMarker?.showInfoWindow()
    }
    
    /**
     * Muestra el marcador de ubicación del usuario (azul)
     */
    fun showUserMarker(latLng: LatLng, title: String = "Mi ubicación") {
        // Remover marcador anterior si existe
        userMarker?.remove()
        
        // Crear nuevo marcador
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        
        userMarker = map.addMarker(markerOptions)
    }
    
    /**
     * Remueve el marcador de origen
     */
    fun removeOrigenMarker() {
        origenMarker?.remove()
        origenMarker = null
    }
    
    /**
     * Remueve el marcador de destino
     */
    fun removeDestinoMarker() {
        destinoMarker?.remove()
        destinoMarker = null
    }
    
    /**
     * Remueve el marcador de usuario
     */
    fun removeUserMarker() {
        userMarker?.remove()
        userMarker = null
    }
    
    /**
     * Remueve todos los marcadores
     */
    fun clearAllMarkers() {
        removeOrigenMarker()
        removeDestinoMarker()
        removeUserMarker()
    }
    
    /**
     * Obtiene el marcador de origen
     */
    fun getOrigenMarker(): Marker? = origenMarker
    
    /**
     * Obtiene el marcador de destino
     */
    fun getDestinoMarker(): Marker? = destinoMarker
    
    /**
     * Obtiene el marcador de usuario
     */
    fun getUserMarker(): Marker? = userMarker
}
