package com.azyroapp.rutasmex.data.local

import androidx.room.*
import com.azyroapp.rutasmex.data.model.FavoriteSearch
import kotlinx.coroutines.flow.Flow

/**
 * DAO para búsquedas favoritas
 */
@Dao
interface FavoriteSearchDao {
    
    @Query("SELECT * FROM favorite_searches ORDER BY lastUsedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteSearch>>
    
    @Query("SELECT * FROM favorite_searches WHERE cityId = :cityId ORDER BY lastUsedAt DESC")
    fun getFavoritesByCity(cityId: String): Flow<List<FavoriteSearch>>
    
    @Query("SELECT * FROM favorite_searches ORDER BY useCount DESC LIMIT :limit")
    fun getMostUsedFavorites(limit: Int = 10): Flow<List<FavoriteSearch>>
    
    @Query("SELECT * FROM favorite_searches WHERE id = :id")
    suspend fun getFavoriteById(id: String): FavoriteSearch?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteSearch)
    
    @Update
    suspend fun updateFavorite(favorite: FavoriteSearch)
    
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteSearch)
    
    @Query("DELETE FROM favorite_searches WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
    
    @Query("DELETE FROM favorite_searches")
    suspend fun deleteAllFavorites()
    
    @Query("UPDATE favorite_searches SET lastUsedAt = :lastUsedAt, useCount = useCount + 1 WHERE id = :id")
    suspend fun incrementUseCount(id: String, lastUsedAt: java.util.Date)
}
