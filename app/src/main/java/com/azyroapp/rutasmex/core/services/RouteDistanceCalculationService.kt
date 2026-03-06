package com.azyroapp.rutasmex.core.services

import android.location.Location
import android.util.Log
import com.azyroapp.rutasmex.core.config.AppConfiguration
import com.azyroapp.rutasmex.data.model.DistanceCalculationMode
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.data.model.RouteDistanceResult
import com.google.android.gms.maps.model.LatLng

/**
 * Servicio centralizado para cálculos de distancia en rutas
 * 
 * Equivalente a RouteDistanceCalculationService.swift de iOS
 * Implementa toda la lógica de proyección, auto-selección y cálculo de distancias
 */
object RouteDistanceCalculationService {
    
    private const val TAG = "RouteDistanceCalc"
    
    /**
     * Calcula distancia siguiendo la ruta de combi
     * 
     * @param userLocation Ubicación actual del usuario
     * @param origin Punto de origen
     * @param destination Punto de destino
     * @param route Ruta seleccionada
     * @param calculationMode Modo de cálculo (IDA/REGRESO/COMPLETO)
     * @return Resultado con todas las proyecciones y distancias, o null si hay error
     */
    fun calculateDistanceAlongRoute(
        userLocation: Location,
        origin: Location,
        destination: Location,
        route: Route,
        calculationMode: DistanceCalculationMode
    ): RouteDistanceResult? {
        
        // Validar que origen y destino sean diferentes
        val distOriginDestination = origin.distanceTo(destination).toDouble()
        if (distOriginDestination <= 10) {
            Log.w(TAG, "Origen y destino son el mismo punto (${distOriginDestination.toInt()}m)")
            return null
        }
        
        // Obtener segmentos de la ruta
        val allSegments = route.coordinates
        if (allSegments.size < 2) {
            Log.w(TAG, "Ruta no tiene suficientes segmentos")
            return null
        }
        
        val ida = allSegments[0].toLatLngList()
        val regreso = allSegments[1].toLatLngList()
        
        // Crear segmento COMPLETO (IDA + REGRESO + IDA + REGRESO)
        val completo = ida + regreso + ida + regreso
        
        // Validar que los segmentos no estén vacíos
        if (ida.isEmpty() || regreso.isEmpty()) {
            Log.w(TAG, "Segmentos de ruta vacíos")
            return null
        }
        
        // Convertir ubicaciones a LatLng
        val userLatLng = LatLng(userLocation.latitude, userLocation.longitude)
        val originLatLng = LatLng(origin.latitude, origin.longitude)
        val destinationLatLng = LatLng(destination.latitude, destination.longitude)
        
        // Proyectar puntos en cada segmento
        val projectedOriginIda = RouteProjectionService.closestCoordinate(originLatLng, ida)
        val projectedUserIda = RouteProjectionService.closestCoordinate(userLatLng, ida)
        val projectedDestinationIda = RouteProjectionService.closestCoordinate(destinationLatLng, ida)
        
        val projectedOriginRegreso = RouteProjectionService.closestCoordinate(originLatLng, regreso)
        val projectedUserRegreso = RouteProjectionService.closestCoordinate(userLatLng, regreso)
        val projectedDestinationRegreso = RouteProjectionService.closestCoordinate(destinationLatLng, regreso)
        
        val projectedOriginCompleto = RouteProjectionService.closestCoordinate(originLatLng, completo)
        val projectedUserCompleto = RouteProjectionService.closestCoordinate(userLatLng, completo)
        val projectedDestinationCompleto = RouteProjectionService.closestCoordinate(destinationLatLng, completo)
        
        // Extraer segmentos entre origen y destino proyectados
        val idaSegment = RouteProjectionService.extractSegmentBetweenPoints(
            ida + regreso + ida + regreso,
            projectedOriginIda,
            projectedDestinationIda
        )
        
        val regresoSegment = RouteProjectionService.extractSegmentBetweenPoints(
            regreso + ida + regreso + ida,
            projectedOriginRegreso,
            projectedDestinationRegreso
        )
        
        val completoSegment = RouteProjectionService.extractSegmentBetweenPoints(
            ida + regreso + ida + regreso,
            projectedOriginCompleto,
            projectedDestinationCompleto
        )
        
        // Validar que al menos un segmento sea válido
        if (idaSegment.isEmpty() && regresoSegment.isEmpty() && completoSegment.isEmpty()) {
            Log.w(TAG, "No se pudieron extraer segmentos de ruta")
            return null
        }
        
        // Auto-seleccionar el mejor modo si es necesario
        val finalMode = autoSelectBestModeIfNeeded(
            currentMode = calculationMode,
            origin = origin,
            destination = destination,
            idaSegment = idaSegment,
            regresoSegment = regresoSegment,
            completoSegment = completoSegment,
            projectedOriginIda = projectedOriginIda,
            projectedDestinationIda = projectedDestinationIda,
            projectedOriginRegreso = projectedOriginRegreso,
            projectedDestinationRegreso = projectedDestinationRegreso,
            projectedOriginCompleto = projectedOriginCompleto,
            projectedDestinationCompleto = projectedDestinationCompleto
        )
        
        // Seleccionar segmento según modo final
        val selectedRouteSegment = when (finalMode) {
            DistanceCalculationMode.IDA -> idaSegment
            DistanceCalculationMode.REGRESO -> regresoSegment
            DistanceCalculationMode.COMPLETO -> completoSegment
        }
        
        if (selectedRouteSegment.isEmpty()) {
            Log.w(TAG, "Segmento seleccionado vacío para modo ${finalMode.displayName()}")
            return null
        }
        
        // Seleccionar proyecciones según modo
        val (projectedOrigin, projectedUser, projectedDestination) = when (finalMode) {
            DistanceCalculationMode.IDA -> 
                Triple(projectedOriginIda, projectedUserIda, projectedDestinationIda)
            DistanceCalculationMode.REGRESO -> 
                Triple(projectedOriginRegreso, projectedUserRegreso, projectedDestinationRegreso)
            DistanceCalculationMode.COMPLETO -> 
                Triple(projectedOriginCompleto, projectedUserCompleto, projectedDestinationCompleto)
        }
        
        // Encontrar índices de los puntos proyectados
        val (currentIndex, distanceToRoute) = RouteProjectionService.findIndexOfProjectedPoint(
            projectedUser, selectedRouteSegment
        )
        val (destinationIndex, distanceDestToRoute) = RouteProjectionService.findIndexOfProjectedPoint(
            projectedDestination, selectedRouteSegment
        )
        val (originIndex, _) = RouteProjectionService.findIndexOfProjectedPoint(
            projectedOrigin, selectedRouteSegment
        )
        
        // Validar índices
        if (currentIndex >= selectedRouteSegment.size || 
            destinationIndex >= selectedRouteSegment.size || 
            originIndex >= selectedRouteSegment.size) {
            Log.w(TAG, "Índices fuera de rango")
            return null
        }
        
        // Validar que el destino esté cerca de la ruta
        if (distanceDestToRoute > AppConfiguration.DESTINATION_PROXIMITY_THRESHOLD) {
            Log.w(TAG, "Destino muy lejos de la ruta: ${distanceDestToRoute.toInt()}m")
            return null
        }
        
        // Calcular distancia usuario → destino
        var distanceAlongRoute = 0.0
        if (currentIndex < destinationIndex) {
            for (i in currentIndex until destinationIndex) {
                distanceAlongRoute += RouteProjectionService.calculateDistance(
                    selectedRouteSegment[i],
                    selectedRouteSegment[i + 1]
                )
            }
        } else if (currentIndex > destinationIndex) {
            for (i in destinationIndex until currentIndex) {
                distanceAlongRoute += RouteProjectionService.calculateDistance(
                    selectedRouteSegment[i],
                    selectedRouteSegment[i + 1]
                )
            }
        }
        
        // Calcular distancia total origen → destino
        var totalDistanceAlongRoute = 0.0
        if (originIndex < destinationIndex) {
            for (i in originIndex until destinationIndex) {
                totalDistanceAlongRoute += RouteProjectionService.calculateDistance(
                    selectedRouteSegment[i],
                    selectedRouteSegment[i + 1]
                )
            }
        } else if (originIndex > destinationIndex) {
            for (i in destinationIndex until originIndex) {
                totalDistanceAlongRoute += RouteProjectionService.calculateDistance(
                    selectedRouteSegment[i],
                    selectedRouteSegment[i + 1]
                )
            }
        }
        
        // Crear segmento usuario → destino (para visualización)
        val userToDestinationSegment = if (currentIndex <= destinationIndex) {
            selectedRouteSegment.subList(currentIndex, destinationIndex + 1)
        } else {
            selectedRouteSegment.subList(destinationIndex, currentIndex + 1).reversed()
        }
        
        return RouteDistanceResult(
            distanceToDestination = distanceAlongRoute,
            totalDistance = totalDistanceAlongRoute,
            selectedRouteSegment = selectedRouteSegment,
            idaRouteSegment = idaSegment,
            regresoRouteSegment = regresoSegment,
            completoRouteSegment = completoSegment,
            userToDestinationSegment = userToDestinationSegment,
            projectedOriginIda = projectedOriginIda,
            projectedUserIda = projectedUserIda,
            projectedDestinationIda = projectedDestinationIda,
            projectedOriginRegreso = projectedOriginRegreso,
            projectedUserRegreso = projectedUserRegreso,
            projectedDestinationRegreso = projectedDestinationRegreso,
            projectedOriginCompleto = projectedOriginCompleto,
            projectedUserCompleto = projectedUserCompleto,
            projectedDestinationCompleto = projectedDestinationCompleto,
            selectedMode = finalMode
        )
    }

    
    /**
     * Auto-selecciona el mejor modo de cálculo si es necesario
     * 
     * Lógica:
     * 1. Calcula distancia origen→proyectado y destino→proyectado para cada modo
     * 2. Filtra candidatos válidos (ambas distancias ≤ 500m)
     * 3. Calcula longitud total de cada segmento candidato
     * 4. Elige el segmento MÁS CORTO
     * 
     * @return Modo seleccionado (puede ser diferente al currentMode)
     */
    private fun autoSelectBestModeIfNeeded(
        currentMode: DistanceCalculationMode,
        origin: Location,
        destination: Location,
        idaSegment: List<LatLng>,
        regresoSegment: List<LatLng>,
        completoSegment: List<LatLng>,
        projectedOriginIda: LatLng,
        projectedDestinationIda: LatLng,
        projectedOriginRegreso: LatLng,
        projectedDestinationRegreso: LatLng,
        projectedOriginCompleto: LatLng,
        projectedDestinationCompleto: LatLng
    ): DistanceCalculationMode {
        
        // TODO: Implementar flag de selección manual
        // Por ahora, siempre auto-selecciona
        
        // Validar que los segmentos no estén vacíos
        if (idaSegment.isEmpty() || regresoSegment.isEmpty() || completoSegment.isEmpty()) {
            Log.w(TAG, "Segmentos vacíos en auto-selección, usando modo actual: ${currentMode.displayName()}")
            return currentMode
        }
        
        // Calcular distancias de proximidad (origen/destino → proyectado)
        val originLatLng = LatLng(origin.latitude, origin.longitude)
        val destinationLatLng = LatLng(destination.latitude, destination.longitude)
        
        val distOriginIdaProj = RouteProjectionService.calculateDistance(originLatLng, projectedOriginIda)
        val distDestIdaProj = RouteProjectionService.calculateDistance(destinationLatLng, projectedDestinationIda)
        
        val distOriginRegresoProj = RouteProjectionService.calculateDistance(originLatLng, projectedOriginRegreso)
        val distDestRegresoProj = RouteProjectionService.calculateDistance(destinationLatLng, projectedDestinationRegreso)
        
        val distOriginCompletoProj = RouteProjectionService.calculateDistance(originLatLng, projectedOriginCompleto)
        val distDestCompletoProj = RouteProjectionService.calculateDistance(destinationLatLng, projectedDestinationCompleto)
        
        // Validar que las distancias sean válidas
        if (!distOriginIdaProj.isFinite() || !distDestIdaProj.isFinite() ||
            !distOriginRegresoProj.isFinite() || !distDestRegresoProj.isFinite() ||
            !distOriginCompletoProj.isFinite() || !distDestCompletoProj.isFinite()) {
            Log.w(TAG, "Distancias inválidas en auto-selección")
            return currentMode
        }
        
        // Calcular longitud de cada segmento
        val longitudIda = RouteProjectionService.calculateSegmentLength(idaSegment)
        val longitudRegreso = RouteProjectionService.calculateSegmentLength(regresoSegment)
        val longitudCompleto = RouteProjectionService.calculateSegmentLength(completoSegment)
        
        // Validar que las longitudes sean válidas
        if (!longitudIda.isFinite() || !longitudRegreso.isFinite() || !longitudCompleto.isFinite()) {
            Log.w(TAG, "Longitudes inválidas en auto-selección")
            return currentMode
        }
        
        Log.i(TAG, "📊 Auto-selección de modo:")
        Log.i(TAG, "   IDA - Origen→Proyectado: ${distOriginIdaProj.toInt()}m, Destino→Proyectado: ${distDestIdaProj.toInt()}m, Longitud: ${longitudIda.toInt()}m")
        Log.i(TAG, "   REGRESO - Origen→Proyectado: ${distOriginRegresoProj.toInt()}m, Destino→Proyectado: ${distDestRegresoProj.toInt()}m, Longitud: ${longitudRegreso.toInt()}m")
        Log.i(TAG, "   COMPLETO - Origen→Proyectado: ${distOriginCompletoProj.toInt()}m, Destino→Proyectado: ${distDestCompletoProj.toInt()}m, Longitud: ${longitudCompleto.toInt()}m")
        
        // Filtrar candidatos válidos (origen→proyectado Y destino→proyectado ≤ 500m)
        val candidatos = mutableListOf<Pair<DistanceCalculationMode, Double>>()
        
        if (distOriginIdaProj <= AppConfiguration.PROXIMITY_THRESHOLD && distDestIdaProj <= AppConfiguration.PROXIMITY_THRESHOLD) {
            candidatos.add(Pair(DistanceCalculationMode.IDA, longitudIda))
            Log.i(TAG, "   ✅ IDA es candidato válido")
        } else {
            Log.i(TAG, "   ❌ IDA descartado (muy lejos)")
        }
        
        if (distOriginRegresoProj <= AppConfiguration.PROXIMITY_THRESHOLD && distDestRegresoProj <= AppConfiguration.PROXIMITY_THRESHOLD) {
            candidatos.add(Pair(DistanceCalculationMode.REGRESO, longitudRegreso))
            Log.i(TAG, "   ✅ REGRESO es candidato válido")
        } else {
            Log.i(TAG, "   ❌ REGRESO descartado (muy lejos)")
        }
        
        if (distOriginCompletoProj <= AppConfiguration.PROXIMITY_THRESHOLD && distDestCompletoProj <= AppConfiguration.PROXIMITY_THRESHOLD) {
            candidatos.add(Pair(DistanceCalculationMode.COMPLETO, longitudCompleto))
            Log.i(TAG, "   ✅ COMPLETO es candidato válido")
        } else {
            Log.i(TAG, "   ❌ COMPLETO descartado (muy lejos)")
        }
        
        // Validar que haya al menos un candidato
        if (candidatos.isEmpty()) {
            Log.w(TAG, "⚠️ No hay candidatos válidos, usando modo actual: ${currentMode.displayName()}")
            return currentMode
        }
        
        // Elegir el candidato con la ruta más corta
        val mejor = candidatos.minByOrNull { it.second }
        
        if (mejor == null || !mejor.second.isFinite() || mejor.second <= 0) {
            Log.w(TAG, "⚠️ Error al seleccionar mejor candidato")
            return currentMode
        }
        
        Log.i(TAG, "✅ Modo auto-seleccionado: ${mejor.first.displayName()} (${mejor.second.toInt()}m)")
        
        return mejor.first
    }
}
