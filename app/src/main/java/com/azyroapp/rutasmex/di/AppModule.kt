package com.azyroapp.rutasmex.di

import android.content.Context
import com.azyroapp.rutasmex.data.local.AppDatabase
import com.azyroapp.rutasmex.data.local.FavoriteSearchDao
import com.azyroapp.rutasmex.data.local.SavedPlaceDao
import com.azyroapp.rutasmex.data.local.TripDao
import com.azyroapp.rutasmex.data.repository.RouteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para proveer dependencias de la aplicación
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideRouteRepository(
        @ApplicationContext context: Context
    ): RouteRepository {
        return RouteRepository(context)
    }
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideTripDao(database: AppDatabase): TripDao {
        return database.tripDao()
    }
    
    @Provides
    @Singleton
    fun provideFavoriteSearchDao(database: AppDatabase): FavoriteSearchDao {
        return database.favoriteSearchDao()
    }
    
    @Provides
    @Singleton
    fun provideSavedPlaceDao(database: AppDatabase): SavedPlaceDao {
        return database.savedPlaceDao()
    }
    
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): com.azyroapp.rutasmex.data.preferences.PreferencesManager {
        return com.azyroapp.rutasmex.data.preferences.PreferencesManager(context)
    }
}
