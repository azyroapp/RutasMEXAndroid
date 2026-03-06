package com.azyroapp.rutasmex.data.local

import androidx.room.*
import com.azyroapp.rutasmex.data.model.PlaceCategory
import com.azyroapp.rutasmex.data.model.SavedPlace
import kotlinx.coroutines.flow.Flow

/**
 * DAO para lugares guardados
 */
@Dao
interface SavedPlaceDao {
    
    @Query("SELECT * FROM saved_places ORDER BY lastUsedAt DESC")
    fun getAllPlaces(): Flow<List<SavedPlace>>
    
    @Query("SELECT * FROM saved_places WHERE category = :category ORDER BY lastUsedAt DESC")
    fun getPlacesByCategory(category: PlaceCategory): Flow<List<SavedPlace>>
    
    @Query("SELECT * FROM saved_places ORDER BY useCount DESC LIMIT :limit")
    fun getMostUsedPlaces(limit: Int = 10): Flow<List<SavedPlace>>
    
    @Query("SELECT * FROM saved_places WHERE id = :id")
    suspend fun getPlaceById(id: String): SavedPlace?
    
    @Query("SELECT * FROM saved_places WHERE name LIKE '%' || :query || '%' ORDER BY lastUsedAt DESC")
    fun searchPlaces(query: String): Flow<List<SavedPlace>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: SavedPlace)
    
    @Update
    suspend fun updatePlace(place: SavedPlace)
    
    @Delete
    suspend fun deletePlace(place: SavedPlace)
    
    @Query("DELETE FROM saved_places WHERE id = :id")
    suspend fun deletePlaceById(id: String)
    
    @Query("DELETE FROM saved_places")
    suspend fun deleteAllPlaces()
    
    @Query("UPDATE saved_places SET lastUsedAt = :lastUsedAt, useCount = useCount + 1 WHERE id = :id")
    suspend fun incrementUseCount(id: String, lastUsedAt: java.util.Date)
}
