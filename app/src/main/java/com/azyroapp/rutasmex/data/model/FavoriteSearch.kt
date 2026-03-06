package com.azyroapp.rutasmex.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Búsqueda favorita guardada por el usuario
 */
@Entity(tableName = "favorite_searches")
data class FavoriteSearch(
    @PrimaryKey
    val id: String,
    
    val name: String,
    
    val cityId: String,
    val cityName: String,
    
    val originLatitude: Double,
    val originLongitude: Double,
    val originName: String,
    
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val destinationName: String,
    
    val originRadius: Double = 200.0,
    val destinationRadius: Double = 200.0,
    
    val createdAt: Date = Date(),
    val lastUsedAt: Date = Date(),
    val useCount: Int = 0
) {
    companion object {
        fun create(
            name: String,
            cityId: String,
            cityName: String,
            origin: LocationPoint,
            destination: LocationPoint,
            originRadius: Double = 200.0,
            destinationRadius: Double = 200.0
        ): FavoriteSearch {
            return FavoriteSearch(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                cityId = cityId,
                cityName = cityName,
                originLatitude = origin.latitude,
                originLongitude = origin.longitude,
                originName = origin.name,
                destinationLatitude = destination.latitude,
                destinationLongitude = destination.longitude,
                destinationName = destination.name,
                originRadius = originRadius,
                destinationRadius = destinationRadius
            )
        }
    }
    
    fun toOriginLocation(): LocationPoint {
        return LocationPoint(
            id = java.util.UUID.randomUUID().toString(),
            name = originName,
            latitude = originLatitude,
            longitude = originLongitude
        )
    }
    
    fun toDestinationLocation(): LocationPoint {
        return LocationPoint(
            id = java.util.UUID.randomUUID().toString(),
            name = destinationName,
            latitude = destinationLatitude,
            longitude = destinationLongitude
        )
    }
}
