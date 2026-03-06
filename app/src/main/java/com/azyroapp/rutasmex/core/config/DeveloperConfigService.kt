package com.azyroapp.rutasmex.core.config

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 🛠️ Servicio centralizado para configuraciones de desarrollador y debug
 * Equivalente a DeveloperConfigService.swift de iOS
 * 
 * Proporciona flags para debugging, testing y visualización de datos internos
 */
class DeveloperConfigService private constructor(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "developer_config",
        Context.MODE_PRIVATE
    )
    
    // MARK: - Debug Flags
    
    private val _showDebugPanel = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_DEBUG_PANEL, false)
    )
    val showDebugPanel: StateFlow<Boolean> = _showDebugPanel.asStateFlow()
    
    private val _showDetailedLogs = MutableStateFlow(
        prefs.getBoolean(KEY_DETAILED_LOGS, false)
    )
    val showDetailedLogs: StateFlow<Boolean> = _showDetailedLogs.asStateFlow()
    
    private val _enableTestMode = MutableStateFlow(
        prefs.getBoolean(KEY_TEST_MODE, false)
    )
    val enableTestMode: StateFlow<Boolean> = _enableTestMode.asStateFlow()
    
    private val _showDeveloperSection = MutableStateFlow(
        prefs.getBoolean(KEY_DEVELOPER_SECTION, false)
    )
    val showDeveloperSection: StateFlow<Boolean> = _showDeveloperSection.asStateFlow()
    
    // MARK: - Route Visualization Flags
    
    private val _showRouteSegments = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_ROUTE_SEGMENTS, false)
    )
    val showRouteSegments: StateFlow<Boolean> = _showRouteSegments.asStateFlow()
    
    private val _showRoutePoints = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_ROUTE_POINTS, false)
    )
    val showRoutePoints: StateFlow<Boolean> = _showRoutePoints.asStateFlow()
    
    private val _showSegmentIda = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_SEGMENT_IDA, false)
    )
    val showSegmentIda: StateFlow<Boolean> = _showSegmentIda.asStateFlow()
    
    private val _showSegmentRegreso = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_SEGMENT_REGRESO, false)
    )
    val showSegmentRegreso: StateFlow<Boolean> = _showSegmentRegreso.asStateFlow()
    
    private val _showSegmentCompleto = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_SEGMENT_COMPLETO, false)
    )
    val showSegmentCompleto: StateFlow<Boolean> = _showSegmentCompleto.asStateFlow()
    
    private val _showPointsIda = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_POINTS_IDA, false)
    )
    val showPointsIda: StateFlow<Boolean> = _showPointsIda.asStateFlow()
    
    private val _showPointsRegreso = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_POINTS_REGRESO, false)
    )
    val showPointsRegreso: StateFlow<Boolean> = _showPointsRegreso.asStateFlow()
    
    private val _showPointsCompleto = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_POINTS_COMPLETO, false)
    )
    val showPointsCompleto: StateFlow<Boolean> = _showPointsCompleto.asStateFlow()
    
    private val _showAnnotationTitles = MutableStateFlow(
        prefs.getBoolean(KEY_SHOW_ANNOTATION_TITLES, false)
    )
    val showAnnotationTitles: StateFlow<Boolean> = _showAnnotationTitles.asStateFlow()
    
    // MARK: - Setters
    
    fun setShowDebugPanel(value: Boolean) {
        _showDebugPanel.value = value
        prefs.edit().putBoolean(KEY_SHOW_DEBUG_PANEL, value).apply()
    }
    
    fun setShowDetailedLogs(value: Boolean) {
        _showDetailedLogs.value = value
        prefs.edit().putBoolean(KEY_DETAILED_LOGS, value).apply()
    }
    
    fun setEnableTestMode(value: Boolean) {
        _enableTestMode.value = value
        prefs.edit().putBoolean(KEY_TEST_MODE, value).apply()
    }
    
    fun setShowDeveloperSection(value: Boolean) {
        _showDeveloperSection.value = value
        prefs.edit().putBoolean(KEY_DEVELOPER_SECTION, value).apply()
    }
    
    fun setShowRouteSegments(value: Boolean) {
        _showRouteSegments.value = value
        prefs.edit().putBoolean(KEY_SHOW_ROUTE_SEGMENTS, value).apply()
    }
    
    fun setShowRoutePoints(value: Boolean) {
        _showRoutePoints.value = value
        prefs.edit().putBoolean(KEY_SHOW_ROUTE_POINTS, value).apply()
    }
    
    fun setShowSegmentIda(value: Boolean) {
        _showSegmentIda.value = value
        prefs.edit().putBoolean(KEY_SHOW_SEGMENT_IDA, value).apply()
    }
    
    fun setShowSegmentRegreso(value: Boolean) {
        _showSegmentRegreso.value = value
        prefs.edit().putBoolean(KEY_SHOW_SEGMENT_REGRESO, value).apply()
    }
    
    fun setShowSegmentCompleto(value: Boolean) {
        _showSegmentCompleto.value = value
        prefs.edit().putBoolean(KEY_SHOW_SEGMENT_COMPLETO, value).apply()
    }
    
    fun setShowPointsIda(value: Boolean) {
        _showPointsIda.value = value
        prefs.edit().putBoolean(KEY_SHOW_POINTS_IDA, value).apply()
    }
    
    fun setShowPointsRegreso(value: Boolean) {
        _showPointsRegreso.value = value
        prefs.edit().putBoolean(KEY_SHOW_POINTS_REGRESO, value).apply()
    }
    
    fun setShowPointsCompleto(value: Boolean) {
        _showPointsCompleto.value = value
        prefs.edit().putBoolean(KEY_SHOW_POINTS_COMPLETO, value).apply()
    }
    
    fun setShowAnnotationTitles(value: Boolean) {
        _showAnnotationTitles.value = value
        prefs.edit().putBoolean(KEY_SHOW_ANNOTATION_TITLES, value).apply()
    }
    
    // MARK: - Quick Actions
    
    /**
     * Alterna el estado del panel de debug
     */
    fun toggleDebugPanel() {
        setShowDebugPanel(!_showDebugPanel.value)
    }
    
    /**
     * Resetea todos los flags a sus valores por defecto (false)
     */
    fun resetAllFlags() {
        setShowDebugPanel(false)
        setShowDetailedLogs(false)
        setEnableTestMode(false)
        setShowDeveloperSection(false)
        setShowRouteSegments(false)
        setShowRoutePoints(false)
        setShowSegmentIda(false)
        setShowSegmentRegreso(false)
        setShowSegmentCompleto(false)
        setShowPointsIda(false)
        setShowPointsRegreso(false)
        setShowPointsCompleto(false)
        setShowAnnotationTitles(false)
    }
    
    // MARK: - Singleton
    
    companion object {
        @Volatile
        private var INSTANCE: DeveloperConfigService? = null
        
        // Keys para SharedPreferences
        private const val KEY_SHOW_DEBUG_PANEL = "developer_show_debug_panel"
        private const val KEY_DETAILED_LOGS = "developer_detailed_logs"
        private const val KEY_TEST_MODE = "developer_test_mode"
        private const val KEY_DEVELOPER_SECTION = "developer_show_section"
        private const val KEY_SHOW_ROUTE_SEGMENTS = "developer_show_route_segments"
        private const val KEY_SHOW_ROUTE_POINTS = "developer_show_route_points"
        private const val KEY_SHOW_SEGMENT_IDA = "developer_show_segment_ida"
        private const val KEY_SHOW_SEGMENT_REGRESO = "developer_show_segment_regreso"
        private const val KEY_SHOW_SEGMENT_COMPLETO = "developer_show_segment_completo"
        private const val KEY_SHOW_POINTS_IDA = "developer_show_points_ida"
        private const val KEY_SHOW_POINTS_REGRESO = "developer_show_points_regreso"
        private const val KEY_SHOW_POINTS_COMPLETO = "developer_show_points_completo"
        private const val KEY_SHOW_ANNOTATION_TITLES = "developer_show_annotation_titles"
        
        /**
         * Obtiene la instancia singleton del servicio
         */
        fun getInstance(context: Context): DeveloperConfigService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DeveloperConfigService(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}
