package com.azyroapp.rutasmex.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.azyroapp.rutasmex.data.model.FavoriteSearch
import com.azyroapp.rutasmex.data.model.SavedPlace
import com.azyroapp.rutasmex.data.model.Trip

/**
 * Base de datos principal de la aplicación
 */
@Database(
    entities = [
        Trip::class,
        FavoriteSearch::class,
        SavedPlace::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun tripDao(): TripDao
    abstract fun favoriteSearchDao(): FavoriteSearchDao
    abstract fun savedPlaceDao(): SavedPlaceDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rutasmex_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
