package com.azyroapp.rutasmex.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.azyroapp.rutasmex.data.model.DistanceCalculationMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Manager para preferencias de la aplicación usando DataStore
 */
class PreferencesManager(private val context: Context) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rutasmex_preferences")
    
    companion object {
        // Keys para preferencias
        private val SELECTED_CITY_ID = stringPreferencesKey("selected_city_id")
        private val SELECTED_CITY_NAME = stringPreferencesKey("selected_city_name")
        private val CALCULATION_MODE = stringPreferencesKey("calculation_mode")
        private val IS_MODE_MANUALLY_SELECTED = booleanPreferencesKey("is_mode_manually_selected")
        private val MAP_TYPE = stringPreferencesKey("map_type")
        private val ORIGIN_LATITUDE = doublePreferencesKey("origin_latitude")
        private val ORIGIN_LONGITUDE = doublePreferencesKey("origin_longitude")
        private val ORIGIN_NAME = stringPreferencesKey("origin_name")
        private val DESTINATION_LATITUDE = doublePreferencesKey("destination_latitude")
        private val DESTINATION_LONGITUDE = doublePreferencesKey("destination_longitude")
        private val DESTINATION_NAME = stringPreferencesKey("destination_name")
        private val ORIGIN_RADIUS = doublePreferencesKey("origin_radius")
        private val DESTINATION_RADIUS = doublePreferencesKey("destination_radius")
        private val LAST_ACTIVE_ROUTE_ID = stringPreferencesKey("last_active_route_id")
        private val LAST_FAVORITE_ORIGIN = stringPreferencesKey("last_favorite_origin")
        private val LAST_FAVORITE_DESTINATION = stringPreferencesKey("last_favorite_destination")
        
        // Preferencias de proximidad
        private val PROXIMITY_DISTANCE = doublePreferencesKey("proximity_distance")
        private val PROXIMITY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("proximity_notifications_enabled")
        private val PROXIMITY_SOUND_ENABLED = booleanPreferencesKey("proximity_sound_enabled")
        private val PROXIMITY_VIBRATION_ENABLED = booleanPreferencesKey("proximity_vibration_enabled")
    }
    
    // ========== CIUDAD ==========
    
    /**
     * Guarda la ciudad seleccionada
     */
    suspend fun saveSelectedCity(cityId: String, cityName: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_CITY_ID] = cityId
            preferences[SELECTED_CITY_NAME] = cityName
        }
    }
    
    /**
     * Obtiene la ciudad seleccionada
     */
    val selectedCity: Flow<Pair<String?, String?>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Pair(
                preferences[SELECTED_CITY_ID],
                preferences[SELECTED_CITY_NAME]
            )
        }
    
    // ========== MODO DE CÁLCULO ==========
    
    /**
     * Guarda el modo de cálculo
     */
    suspend fun saveCalculationMode(mode: DistanceCalculationMode, isManual: Boolean = false) {
        context.dataStore.edit { preferences ->
            preferences[CALCULATION_MODE] = mode.name
            preferences[IS_MODE_MANUALLY_SELECTED] = isManual
        }
    }
    
    /**
     * Obtiene el modo de cálculo
     */
    val calculationMode: Flow<DistanceCalculationMode> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val modeName = preferences[CALCULATION_MODE] ?: DistanceCalculationMode.IDA.name
            try {
                DistanceCalculationMode.valueOf(modeName)
            } catch (e: IllegalArgumentException) {
                DistanceCalculationMode.IDA
            }
        }
    
    /**
     * Obtiene si el modo fue seleccionado manualmente
     */
    val isModeManuallySelected: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_MODE_MANUALLY_SELECTED] ?: false
        }
    
    // ========== TIPO DE MAPA ==========
    
    /**
     * Guarda el tipo de mapa
     */
    suspend fun saveMapType(mapType: String) {
        context.dataStore.edit { preferences ->
            preferences[MAP_TYPE] = mapType
        }
    }
    
    /**
     * Obtiene el tipo de mapa
     */
    val mapType: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[MAP_TYPE] ?: "NORMAL"
        }
    
    // ========== UBICACIONES ==========
    
    /**
     * Guarda el origen
     */
    suspend fun saveOrigin(latitude: Double, longitude: Double, name: String) {
        context.dataStore.edit { preferences ->
            preferences[ORIGIN_LATITUDE] = latitude
            preferences[ORIGIN_LONGITUDE] = longitude
            preferences[ORIGIN_NAME] = name
        }
    }
    
    /**
     * Obtiene el origen
     */
    val origin: Flow<Triple<Double?, Double?, String?>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Triple(
                preferences[ORIGIN_LATITUDE],
                preferences[ORIGIN_LONGITUDE],
                preferences[ORIGIN_NAME]
            )
        }
    
    /**
     * Guarda el destino
     */
    suspend fun saveDestination(latitude: Double, longitude: Double, name: String) {
        context.dataStore.edit { preferences ->
            preferences[DESTINATION_LATITUDE] = latitude
            preferences[DESTINATION_LONGITUDE] = longitude
            preferences[DESTINATION_NAME] = name
        }
    }
    
    /**
     * Obtiene el destino
     */
    val destination: Flow<Triple<Double?, Double?, String?>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Triple(
                preferences[DESTINATION_LATITUDE],
                preferences[DESTINATION_LONGITUDE],
                preferences[DESTINATION_NAME]
            )
        }
    
    /**
     * Limpia origen y destino
     */
    suspend fun clearLocations() {
        context.dataStore.edit { preferences ->
            preferences.remove(ORIGIN_LATITUDE)
            preferences.remove(ORIGIN_LONGITUDE)
            preferences.remove(ORIGIN_NAME)
            preferences.remove(DESTINATION_LATITUDE)
            preferences.remove(DESTINATION_LONGITUDE)
            preferences.remove(DESTINATION_NAME)
        }
    }
    
    // ========== RADIOS DE BÚSQUEDA ==========
    
    /**
     * Guarda los radios de búsqueda
     */
    suspend fun saveSearchRadii(originRadius: Double, destinationRadius: Double) {
        context.dataStore.edit { preferences ->
            preferences[ORIGIN_RADIUS] = originRadius
            preferences[DESTINATION_RADIUS] = destinationRadius
        }
    }
    
    /**
     * Obtiene los radios de búsqueda
     */
    val searchRadii: Flow<Pair<Double, Double>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Pair(
                preferences[ORIGIN_RADIUS] ?: 200.0,
                preferences[DESTINATION_RADIUS] ?: 200.0
            )
        }
    
    // ========== RUTA ACTIVA ==========
    
    /**
     * Guarda la última ruta activa
     */
    suspend fun saveLastActiveRoute(routeId: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_ACTIVE_ROUTE_ID] = routeId
        }
    }
    
    /**
     * Obtiene la última ruta activa
     */
    val lastActiveRoute: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[LAST_ACTIVE_ROUTE_ID]
        }
    
    // ========== FAVORITOS ==========
    
    /**
     * Guarda el último favorito
     */
    suspend fun saveLastFavorite(originName: String, destinationName: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_FAVORITE_ORIGIN] = originName
            preferences[LAST_FAVORITE_DESTINATION] = destinationName
        }
    }
    
    /**
     * Obtiene el último favorito
     */
    val lastFavorite: Flow<Pair<String?, String?>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            Pair(
                preferences[LAST_FAVORITE_ORIGIN],
                preferences[LAST_FAVORITE_DESTINATION]
            )
        }
    
    // ========== PROXIMIDAD ==========
    
    /**
     * Guarda la configuración de proximidad
     */
    suspend fun saveProximityConfig(
        distance: Double,
        notificationsEnabled: Boolean,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[PROXIMITY_DISTANCE] = distance
            preferences[PROXIMITY_NOTIFICATIONS_ENABLED] = notificationsEnabled
            preferences[PROXIMITY_SOUND_ENABLED] = soundEnabled
            preferences[PROXIMITY_VIBRATION_ENABLED] = vibrationEnabled
        }
    }
    
    /**
     * Obtiene la distancia de proximidad
     */
    val proximityDistance: Flow<Double> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PROXIMITY_DISTANCE] ?: 200.0
        }
    
    /**
     * Obtiene la configuración completa de proximidad
     */
    val proximityConfig: Flow<ProximityConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ProximityConfig(
                distance = preferences[PROXIMITY_DISTANCE] ?: 200.0,
                notificationsEnabled = preferences[PROXIMITY_NOTIFICATIONS_ENABLED] ?: true,
                soundEnabled = preferences[PROXIMITY_SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[PROXIMITY_VIBRATION_ENABLED] ?: true
            )
        }
    
    /**
     * Limpia todas las preferencias
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

/**
 * Data class para configuración de proximidad
 */
data class ProximityConfig(
    val distance: Double = 200.0,
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
