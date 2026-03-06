package com.azyroapp.rutasmex.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.azyroapp.rutasmex.data.local.Converters
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

/**
 * Modelo de datos para un viaje
 */
@Parcelize
@Entity(tableName = "trips")
@TypeConverters(Converters::class)
data class Trip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    // Información básica
    val cityId: String,
    val cityName: String,
    val routeId: String,
    val routeName: String,
    
    // Ubicaciones
    val originLatitude: Double,
    val originLongitude: Double,
    val originName: String,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val destinationName: String,
    
    // Tiempos
    val startTime: Date,
    val endTime: Date? = null,
    
    // Distancias
    val totalDistance: Double,  // Distancia total calculada (metros)
    val distanceTraveled: Double = 0.0,  // Distancia recorrida (metros)
    
    // Modo de cálculo
    val calculationMode: String,  // IDA, REGRESO, COMPLETO
    
    // Estado
    val isCompleted: Boolean = false,
    val isCancelled: Boolean = false,
    
    // Ruta calculada (JSON serializado)
    val calculatedRouteSegment: String? = null,
    
    // Ruta del usuario (JSON serializado)
    val userPath: String? = null,
    
    // Velocidad promedio (km/h)
    val averageSpeed: Double = 0.0,
    
    // Duración (segundos)
    val duration: Long = 0
) : Parcelable {
    /**
     * Calcula el progreso del viaje (0.0 - 1.0)
     */
    fun getProgress(): Float {
        if (totalDistance <= 0) return 0f
        return (distanceTraveled / totalDistance).toFloat().coerceIn(0f, 1f)
    }
    
    /**
     * Calcula la duración en minutos
     */
    fun getDurationMinutes(): Long {
        return duration / 60
    }
    
    /**
     * Verifica si el viaje está activo
     */
    fun isActive(): Boolean {
        return !isCompleted && !isCancelled && endTime == null
    }
    
    /**
     * Formatea la distancia para mostrar
     */
    fun getFormattedDistance(): String {
        return if (totalDistance >= 1000) {
            String.format("%.2f km", totalDistance / 1000)
        } else {
            String.format("%.0f m", totalDistance)
        }
    }
    
    /**
     * Formatea la duración para mostrar
     */
    fun getFormattedDuration(): String {
        val minutes = getDurationMinutes()
        return if (minutes >= 60) {
            val hours = minutes / 60
            val mins = minutes % 60
            "${hours}h ${mins}min"
        } else {
            "${minutes}min"
        }
    }
}

/**
 * Estado del viaje activo
 */
data class ActiveTripState(
    val trip: Trip,
    val currentDistanceToDestination: Double,
    val estimatedTimeMinutes: Int,
    val progress: Float
)
