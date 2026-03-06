package com.azyroapp.rutasmex.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Lugar guardado por el usuario
 */
@Entity(tableName = "saved_places")
data class SavedPlace(
    @PrimaryKey
    val id: String,
    
    val name: String,
    val address: String? = null,
    
    val latitude: Double,
    val longitude: Double,
    
    val category: PlaceCategory = PlaceCategory.OTHER,
    
    val createdAt: Date = Date(),
    val lastUsedAt: Date = Date(),
    val useCount: Int = 0
) {
    companion object {
        fun create(
            name: String,
            latitude: Double,
            longitude: Double,
            address: String? = null,
            category: PlaceCategory = PlaceCategory.OTHER
        ): SavedPlace {
            return SavedPlace(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                address = address,
                latitude = latitude,
                longitude = longitude,
                category = category
            )
        }
    }
    
    fun toLocationPoint(): LocationPoint {
        return LocationPoint(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude
        )
    }
}

/**
 * Categorías de lugares
 */
enum class PlaceCategory {
    HOME,       // Casa
    WORK,       // Trabajo
    SCHOOL,     // Escuela
    FAVORITE,   // Favorito
    OTHER       // Otro
}
