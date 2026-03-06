package com.azyroapp.rutasmex.di

import android.content.Context
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
}
