package com.azyroapp.rutasmex.core.services

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

/**
 * Servicio para proyectar puntos sobre rutas
 * 
 * Equivalente a MKPolyline.closestCoordinate() de iOS
 * Proyecta un punto sobre la geometría de una polilínea
 */
object RouteProjectionService {
    
    /**
     * Encuentra el punto más cercano en una polilínea a un punto dado
     * 
     * @param point Punto a proyectar
     * @param polyline Lista de coordenadas de la polilínea
     * @return Punto proyectado sobre la polilínea
     */
    fun closestCoordinate(point: LatLng, polyline: List<LatLng>): LatLng {
        if (polyline.isEmpty()) return point
        if (polyline.size == 1) return polyline[0]
        
        var closestPoint = polyline[0]
        var minDistance = Double.MAX_VALUE
        
        // Iterar sobre cada segmento de la polilínea
        for (i in 0 until polyline.size - 1) {
            val segmentStart = polyline[i]
            val segmentEnd = polyline[i + 1]
            
            // Proyectar el punto sobre este segmento
            val projectedPoint = projectPointOntoSegment(point, segmentStart, segmentEnd)
            
            // Calcular distancia al punto proyectado
            val distance = calculateDistance(point, projectedPoint)
            
            if (distance < minDistance) {
                minDistance = distance
                closestPoint = projectedPoint
            }
        }
        
        return closestPoint
    }
    
    /**
     * Proyecta un punto sobre un segmento de línea
     * 
     * Usa proyección perpendicular para encontrar el punto más cercano
     * en el segmento [start, end]
     * 
     * @param point Punto a proyectar
     * @param segmentStart Inicio del segmento
     * @param segmentEnd Fin del segmento
     * @return Punto proyectado sobre el segmento
     */
    fun projectPointOntoSegment(
        point: LatLng,
        segmentStart: LatLng,
        segmentEnd: LatLng
    ): LatLng {
        val px = point.longitude
        val py = point.latitude
        val x1 = segmentStart.longitude
        val y1 = segmentStart.latitude
        val x2 = segmentEnd.longitude
        val y2 = segmentEnd.latitude
        
        val dx = x2 - x1
        val dy = y2 - y1
        
        // Si el segmento es un punto, retornar ese punto
        if (dx == 0.0 && dy == 0.0) {
            return segmentStart
        }
        
        // Calcular el parámetro t de la proyección
        // t = 0 → punto está en segmentStart
        // t = 1 → punto está en segmentEnd
        // 0 < t < 1 → punto está entre start y end
        val t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy)
        
        // Limitar t al rango [0, 1] para mantener el punto en el segmento
        val tClamped = t.coerceIn(0.0, 1.0)
        
        // Calcular el punto proyectado
        val projectedLat = y1 + tClamped * dy
        val projectedLon = x1 + tClamped * dx
        
        return LatLng(projectedLat, projectedLon)
    }
    
    /**
     * Calcula la distancia en metros entre dos puntos usando Haversine
     * 
     * @param point1 Primer punto
     * @param point2 Segundo punto
     * @return Distancia en metros
     */
    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            point1.latitude, point1.longitude,
            point2.latitude, point2.longitude,
            results
        )
        return results[0].toDouble()
    }
    
    /**
     * Encuentra el índice del punto más cercano en una lista de coordenadas
     * 
     * @param projectedPoint Punto proyectado
     * @param segment Lista de coordenadas
     * @return Par (índice, distancia al punto más cercano)
     */
    fun findIndexOfProjectedPoint(
        projectedPoint: LatLng,
        segment: List<LatLng>
    ): Pair<Int, Double> {
        var closestIndex = 0
        var minDistance = Double.MAX_VALUE
        
        segment.forEachIndexed { index, coord ->
            val distance = calculateDistance(projectedPoint, coord)
            if (distance < minDistance) {
                minDistance = distance
                closestIndex = index
            }
        }
        
        return Pair(closestIndex, minDistance)
    }
    
    /**
     * Calcula la distancia desde un punto a un segmento de línea
     * 
     * @param point Punto
     * @param segmentStart Inicio del segmento
     * @param segmentEnd Fin del segmento
     * @return Distancia en metros
     */
    fun distanceFromPointToSegment(
        point: LatLng,
        segmentStart: LatLng,
        segmentEnd: LatLng
    ): Double {
        val projectedPoint = projectPointOntoSegment(point, segmentStart, segmentEnd)
        return calculateDistance(point, projectedPoint)
    }
    
    /**
     * Extrae un segmento de ruta entre dos puntos proyectados
     * 
     * @param coordinates Lista completa de coordenadas
     * @param startPoint Punto de inicio proyectado
     * @param endPoint Punto de fin proyectado
     * @return Segmento extraído entre los dos puntos
     */
    fun extractSegmentBetweenPoints(
        coordinates: List<LatLng>,
        startPoint: LatLng,
        endPoint: LatLng
    ): List<LatLng> {
        if (coordinates.size < 2) return emptyList()
        
        var startIndex: Int? = null
        var endIndex: Int? = null
        val tolerance = 1.0 // metros
        
        // Buscar índices de inicio y fin
        for (i in 0 until coordinates.size - 1) {
            val segStart = coordinates[i]
            val segEnd = coordinates[i + 1]
            
            // Buscar índice de inicio
            if (startIndex == null) {
                val distToSegment = distanceFromPointToSegment(startPoint, segStart, segEnd)
                if (distToSegment < tolerance) {
                    startIndex = i
                }
            }
            
            // Buscar índice de fin (solo después de encontrar inicio)
            if (startIndex != null && endIndex == null) {
                val distToSegment = distanceFromPointToSegment(endPoint, segStart, segEnd)
                if (distToSegment < tolerance) {
                    endIndex = i + 1
                    break
                }
            }
        }
        
        // Si no se encontraron índices, retornar vacío
        if (startIndex == null || endIndex == null) return emptyList()
        
        // Construir segmento
        val segment = mutableListOf<LatLng>()
        segment.add(startPoint)
        
        // Agregar puntos intermedios
        if (startIndex + 1 <= endIndex - 1) {
            for (i in (startIndex + 1)..(endIndex - 1)) {
                segment.add(coordinates[i])
            }
        }
        
        segment.add(endPoint)
        
        return segment
    }
    
    /**
     * Calcula la longitud total de un segmento de ruta
     * 
     * @param coordinates Lista de coordenadas del segmento
     * @return Longitud total en metros
     */
    fun calculateSegmentLength(coordinates: List<LatLng>): Double {
        if (coordinates.size < 2) return 0.0
        
        var totalDistance = 0.0
        for (i in 0 until coordinates.size - 1) {
            totalDistance += calculateDistance(coordinates[i], coordinates[i + 1])
        }
        
        return totalDistance
    }
}
