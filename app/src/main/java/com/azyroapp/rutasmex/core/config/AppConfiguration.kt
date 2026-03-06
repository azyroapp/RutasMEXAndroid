package com.azyroapp.rutasmex.core.config

import android.location.Location

/**
 * 🎯 Configuración global de la aplicación
 * Equivalente a AppConfiguration.swift de iOS
 * 
 * Centraliza todas las constantes y configuraciones de la app
 */
object AppConfiguration {
    
    // MARK: - 🌍 Ambiente
    
    /**
     * Ambiente actual de la aplicación
     * Por ahora, los valores son iguales en DEBUG y RELEASE
     */
    enum class Environment {
        DEBUG,
        RELEASE;
        
        companion object {
            // Por defecto usamos RELEASE ya que los valores son idénticos
            val current: Environment = RELEASE
        }
    }
    
    // MARK: - 🥽 Features
    
    /**
     * 🚩 Feature Flag: Habilitar/deshabilitar Realidad Aumentada
     * 
     * **Estado Actual:** `false` (Deshabilitado)
     * 
     * **Razón:** AR está en desarrollo y consume muchos recursos:
     * - 🔋 Batería: +40% consumo/hora (cámara + sensores + GPU)
     * - 💾 RAM: +200 MB cuando está activo
     * - ⚡ CPU/GPU: +50-60% uso continuo
     * - 🌡️ Temperatura: +5-8°C en el dispositivo
     * 
     * **Para habilitar:**
     * 1. Cambiar a `true`
     * 2. Recompilar la app
     * 3. El botón AR aparecerá automáticamente en el toolbar
     * 
     * **Nota:** Cuando está deshabilitado, NINGÚN componente AR se carga en memoria.
     * La app funciona exactamente igual sin consumo adicional de recursos.
     */
    const val IS_AR_ENABLED = false
    
    // MARK: - 📏 Radios de Proximidad
    
    /**
     * Configuración de radios según ambiente
     */
    private data class RadiusConfig(
        val minimum: Double,
        val maximum: Double,
        val step: Double,
        val defaultFar: Double,
        val defaultMedium: Double,
        val defaultNear: Double
    ) {
        companion object {
            fun forEnvironment(env: Environment): RadiusConfig {
                return when (env) {
                    Environment.DEBUG -> RadiusConfig(
                        minimum = 50.0,
                        maximum = 1000.0,
                        step = 25.0,
                        defaultFar = 500.0,
                        defaultMedium = 300.0,
                        defaultNear = 100.0
                    )
                    Environment.RELEASE -> RadiusConfig(
                        minimum = 50.0,
                        maximum = 1000.0,
                        step = 25.0,
                        defaultFar = 500.0,
                        defaultMedium = 300.0,
                        defaultNear = 100.0
                    )
                }
            }
        }
    }
    
    private val radiusConfig = RadiusConfig.forEnvironment(Environment.current)
    
    /** Rango mínimo permitido (metros) */
    val RADIUS_MINIMUM: Double = radiusConfig.minimum
    
    /** Rango máximo permitido (metros) */
    val RADIUS_MAXIMUM: Double = radiusConfig.maximum
    
    /** Incremento del slider (metros) */
    val RADIUS_STEP: Double = radiusConfig.step
    
    /** Valor por defecto - Alerta Temprana (metros) */
    val DEFAULT_FAR_RADIUS: Double = radiusConfig.defaultFar
    
    /** Valor por defecto - Alerta Principal (metros) */
    val DEFAULT_MEDIUM_RADIUS: Double = radiusConfig.defaultMedium
    
    /** Valor por defecto - Alerta Final (metros) */
    val DEFAULT_NEAR_RADIUS: Double = radiusConfig.defaultNear
    
    /** Radio por defecto para búsqueda de origen/destino (metros) */
    const val DEFAULT_SEARCH_RADIUS: Double = 200.0
    
    // MARK: - 📍 GPS y Ubicación
    
    /** Intervalo de actualización de ubicación (milisegundos) */
    const val LOCATION_UPDATE_INTERVAL: Long = 3000L // 3 segundos
    
    /** Intervalo más rápido de actualización (milisegundos) */
    const val LOCATION_FASTEST_INTERVAL: Long = 1000L // 1 segundo
    
    /** Distancia mínima para actualización de ubicación (metros) */
    const val LOCATION_DISTANCE_FILTER: Float = 10f
    
    /** Distancia mínima entre puntos del tracking (línea verde) */
    const val TRACKING_MINIMUM_DISTANCE: Float = 15f
    
    /** Prioridad de ubicación (alta precisión) */
    const val LOCATION_PRIORITY = com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
    
    // MARK: - ⏱️ Tiempos y Delays
    
    /**
     * Configuración de tiempos según ambiente
     */
    private data class TimingConfig(
        val geofenceDebounce: Long
    ) {
        companion object {
            fun forEnvironment(env: Environment): TimingConfig {
                return when (env) {
                    Environment.DEBUG -> TimingConfig(geofenceDebounce = 100L)
                    Environment.RELEASE -> TimingConfig(geofenceDebounce = 300L)
                }
            }
        }
    }
    
    private val timingConfig = TimingConfig.forEnvironment(Environment.current)
    
    /** Debounce para reconfiguración de geofences (milisegundos) */
    val GEOFENCE_DEBOUNCE: Long = timingConfig.geofenceDebounce
    
    /** Throttle para cálculos de ruta (segundos) */
    const val ROUTE_CALCULATION_THROTTLE: Long = 3L
    
    /** Delay para mostrar modal de llegada (milisegundos) */
    const val ARRIVAL_MODAL_DELAY: Long = 100L
    
    /** Delay para verificar geofences iniciales (milisegundos) */
    const val INITIAL_GEOFENCE_CHECK_DELAY: Long = 1000L
    
    /** Delay para animaciones de UI (milisegundos) */
    const val UI_ANIMATION_DELAY: Long = 200L
    
    /** Delay para recargar estado después de viaje (milisegundos) */
    const val TRIP_STATE_RELOAD_DELAY: Long = 500L
    
    /** Delay entre notificaciones de proximidad (milisegundos) */
    const val NOTIFICATION_DELAY_MEDIUM: Long = 10000L // 10 segundos
    const val NOTIFICATION_DELAY_FINAL: Long = 20000L // 20 segundos
    
    /** Delay para trigger de notificación inmediata (milisegundos) */
    const val NOTIFICATION_TRIGGER_DELAY: Long = 500L
    
    /** Duración de validez del cache de suscripciones (milisegundos) */
    const val SUBSCRIPTION_CACHE_VALIDITY: Long = 3600000L // 1 hora
    
    /** Timeout para requests de documentos legales (milisegundos) */
    const val LEGAL_DOCUMENT_TIMEOUT: Long = 10000L
    
    /** Duración por defecto de toasts (milisegundos) */
    const val TOAST_DEFAULT_DURATION: Long = 5000L
    
    /** Intervalo de throttling para logs (milisegundos) */
    const val LOG_THROTTLE_INTERVAL: Long = 5000L
    
    // MARK: - 🚌 Velocidades y Tiempos de Viaje
    
    /** Velocidad promedio de transporte público urbano (metros/minuto) */
    const val AVERAGE_PUBLIC_TRANSPORT_SPEED: Double = 20000.0 / 60.0 // 20 km/h = 333.33 m/min
    
    /** Velocidad promedio para rutas foráneas largas (km/h) */
    const val AVERAGE_SPEED_FORANEA_LARGA: Double = 60.0
    
    /** Velocidad promedio para rutas foráneas (km/h) */
    const val AVERAGE_SPEED_FORANEA: Double = 40.0
    
    /** Velocidad promedio para rutas urbanas (km/h) */
    const val AVERAGE_SPEED_URBANA: Double = 20.0
    
    // MARK: - 📊 Clasificación de Rutas
    
    /** Distancia máxima para ruta foránea corta (km) */
    const val FORANEA_SHORT_MAX_DISTANCE: Double = 20.0
    
    /** Distancia máxima para ruta foránea media (km) */
    const val FORANEA_MEDIUM_MAX_DISTANCE: Double = 50.0
    
    /** Distancia máxima para ruta foránea larga (km) */
    const val FORANEA_LONG_MAX_DISTANCE: Double = 100.0
    
    /** Distancia máxima para ruta urbana corta (km) */
    const val URBANA_SHORT_MAX_DISTANCE: Double = 5.0
    
    /** Distancia máxima para ruta urbana media (km) */
    const val URBANA_MEDIUM_MAX_DISTANCE: Double = 15.0
    
    /** Distancia máxima para ruta urbana larga (km) */
    const val URBANA_LONG_MAX_DISTANCE: Double = 30.0
    
    // MARK: - 🗺️ Mapa y Visualización
    
    /** Intervalo entre puntos de ruta foránea (km) */
    const val ROUTE_POINT_INTERVAL_FORANEA: Double = 5.0
    
    /** Intervalo entre puntos de ruta urbana (metros) */
    const val ROUTE_POINT_INTERVAL_URBANA: Double = 200.0
    
    /** Distancia máxima para renderizado AR (metros) */
    const val AR_MAX_RENDER_DISTANCE: Double = 50.0
    
    /** Altura mínima para rutas AR (metros) */
    const val AR_MIN_HEIGHT: Float = 0.5f
    
    /** Altura máxima para rutas AR (metros) */
    const val AR_MAX_HEIGHT: Float = 10.0f
    
    /** Paso de ajuste de altura AR (metros) */
    const val AR_HEIGHT_STEP: Float = 0.5f
    
    // MARK: - 📳 Vibraciones y Hápticos
    
    /** Intervalo entre pulsos de vibración (milisegundos) */
    const val HAPTIC_PULSE_INTERVAL: Long = 300L
    
    /** Delay entre vibraciones dobles (milisegundos) */
    const val HAPTIC_DOUBLE_DELAY: Long = 100L
    
    /** Intervalo para vibraciones de alerta (milisegundos) */
    const val HAPTIC_ALERT_INTERVAL: Long = 150L
    
    /** Duración de vibración continua (milisegundos) */
    const val HAPTIC_CONTINUOUS_DURATION: Long = 5000L
    
    /** Intervalo de vibración continua (milisegundos) */
    const val HAPTIC_CONTINUOUS_INTERVAL: Long = 400L
    
    // MARK: - 🎨 UI y Animaciones
    
    /** Umbral para considerar que el main thread está bloqueado (milisegundos) */
    const val MAIN_THREAD_BLOCK_THRESHOLD: Long = 16L // 16ms = 60fps
    
    /** Factor de escala mínimo para textos adaptativos */
    const val TEXT_MINIMUM_SCALE_FACTOR: Float = 0.8f
    
    // MARK: - 🔄 Conversiones
    
    /** Factor de conversión metros a kilómetros */
    const val METERS_TO_KILOMETERS: Double = 1000.0
    
    /** Factor de conversión horas a minutos */
    const val HOURS_TO_MINUTES: Double = 60.0
    
    /** Factor de conversión segundos a horas */
    const val SECONDS_TO_HOURS: Double = 3600.0
    
    // MARK: - 🔧 Umbrales de Proximidad
    
    /** Umbral de proximidad general (metros) */
    const val PROXIMITY_THRESHOLD: Double = 500.0
    
    /** Umbral de proximidad al destino (metros) */
    const val DESTINATION_PROXIMITY_THRESHOLD: Double = 300.0
    
    // MARK: - 🎯 Helpers
    
    /**
     * Convierte metros a kilómetros
     */
    fun toKilometers(meters: Double): Double {
        return meters / METERS_TO_KILOMETERS
    }
    
    /**
     * Convierte kilómetros a metros
     */
    fun toMeters(kilometers: Double): Double {
        return kilometers * METERS_TO_KILOMETERS
    }
    
    /**
     * Calcula tiempo estimado en minutos dada una distancia en metros
     */
    fun estimatedTime(distanceInMeters: Double): Int {
        return (distanceInMeters / AVERAGE_PUBLIC_TRANSPORT_SPEED).toInt()
    }
    
    /**
     * Calcula velocidad promedio en km/h dada distancia (km) y duración (segundos)
     */
    fun averageSpeed(distanceInKm: Double, durationInSeconds: Long): Double {
        if (durationInSeconds <= 0) return 0.0
        return distanceInKm / (durationInSeconds / SECONDS_TO_HOURS)
    }
    
    /**
     * Calcula la distancia entre dos ubicaciones (metros)
     */
    fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
