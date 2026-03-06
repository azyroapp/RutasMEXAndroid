package com.azyroapp.rutasmex.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * Resultado del cálculo de distancia en una ruta
 * 
 * Contiene todas las proyecciones, segmentos y distancias calculadas
 * para los 3 modos (IDA, REGRESO, COMPLETO)
 */
data class RouteDistanceResult(
    // Distancias calculadas
    val distanceToDestination: Double,  // Distancia restante al destino (metros)
    val totalDistance: Double,          // Distancia total del viaje (metros)
    
    // Segmentos de ruta
    val selectedRouteSegment: List<LatLng>,      // Segmento elegido según modo
    val idaRouteSegment: List<LatLng>,           // Segmento IDA (origen → destino)
    val regresoRouteSegment: List<LatLng>,       // Segmento REGRESO (origen → destino)
    val completoRouteSegment: List<LatLng>,      // Segmento COMPLETO (origen → destino)
    val userToDestinationSegment: List<LatLng>,  // Usuario → Destino (para visualización)
    
    // Proyecciones en IDA
    val projectedOriginIda: LatLng,
    val projectedUserIda: LatLng,
    val projectedDestinationIda: LatLng,
    
    // Proyecciones en REGRESO
    val projectedOriginRegreso: LatLng,
    val projectedUserRegreso: LatLng,
    val projectedDestinationRegreso: LatLng,
    
    // Proyecciones en COMPLETO
    val projectedOriginCompleto: LatLng,
    val projectedUserCompleto: LatLng,
    val projectedDestinationCompleto: LatLng,
    
    // Modo seleccionado (puede ser auto-seleccionado)
    val selectedMode: DistanceCalculationMode
) {
    /**
     * Obtiene las proyecciones según el modo seleccionado
     */
    fun getProjectionsForMode(mode: DistanceCalculationMode): Triple<LatLng, LatLng, LatLng> {
        return when (mode) {
            DistanceCalculationMode.IDA -> 
                Triple(projectedOriginIda, projectedUserIda, projectedDestinationIda)
            DistanceCalculationMode.REGRESO -> 
                Triple(projectedOriginRegreso, projectedUserRegreso, projectedDestinationRegreso)
            DistanceCalculationMode.COMPLETO -> 
                Triple(projectedOriginCompleto, projectedUserCompleto, projectedDestinationCompleto)
        }
    }
    
    /**
     * Obtiene el segmento según el modo
     */
    fun getSegmentForMode(mode: DistanceCalculationMode): List<LatLng> {
        return when (mode) {
            DistanceCalculationMode.IDA -> idaRouteSegment
            DistanceCalculationMode.REGRESO -> regresoRouteSegment
            DistanceCalculationMode.COMPLETO -> completoRouteSegment
        }
    }
}
