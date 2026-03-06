package com.azyroapp.rutasmex.data.local

import androidx.room.*
import com.azyroapp.rutasmex.data.model.Trip
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceso a la base de datos de viajes
 */
@Dao
interface TripDao {
    
    /**
     * Inserta un nuevo viaje
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)
    
    /**
     * Actualiza un viaje existente
     */
    @Update
    suspend fun updateTrip(trip: Trip)
    
    /**
     * Elimina un viaje
     */
    @Delete
    suspend fun deleteTrip(trip: Trip)
    
    /**
     * Obtiene un viaje por ID
     */
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): Trip?
    
    /**
     * Obtiene todos los viajes ordenados por fecha (más reciente primero)
     */
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<Trip>>
    
    /**
     * Obtiene viajes completados
     */
    @Query("SELECT * FROM trips WHERE isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedTrips(): Flow<List<Trip>>
    
    /**
     * Obtiene viajes de una ciudad específica
     */
    @Query("SELECT * FROM trips WHERE cityId = :cityId ORDER BY startTime DESC")
    fun getTripsByCity(cityId: String): Flow<List<Trip>>
    
    /**
     * Obtiene viajes de una ruta específica
     */
    @Query("SELECT * FROM trips WHERE routeId = :routeId ORDER BY startTime DESC")
    fun getTripsByRoute(routeId: String): Flow<List<Trip>>
    
    /**
     * Obtiene el viaje activo (no completado ni cancelado)
     */
    @Query("SELECT * FROM trips WHERE isCompleted = 0 AND isCancelled = 0 AND endTime IS NULL LIMIT 1")
    suspend fun getActiveTrip(): Trip?
    
    /**
     * Elimina todos los viajes
     */
    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()
    
    /**
     * Cuenta el total de viajes
     */
    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripCount(): Int
    
    /**
     * Obtiene los últimos N viajes
     */
    @Query("SELECT * FROM trips ORDER BY startTime DESC LIMIT :limit")
    fun getRecentTrips(limit: Int): Flow<List<Trip>>
}
