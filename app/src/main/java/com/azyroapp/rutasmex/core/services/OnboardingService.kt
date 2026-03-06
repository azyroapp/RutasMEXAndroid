package com.azyroapp.rutasmex.core.services

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para gestionar el estado del onboarding
 * Paridad 100% con iOS OnboardingService
 */
@Singleton
class OnboardingService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "onboarding"
        private const val HAS_SEEN_ONBOARDING_KEY = "hasSeenOnboarding"
    }
    
    /**
     * Indica si el usuario ya ha visto el onboarding
     */
    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(HAS_SEEN_ONBOARDING_KEY, false)
        private set(value) {
            prefs.edit().putBoolean(HAS_SEEN_ONBOARDING_KEY, value).apply()
        }
    
    /**
     * Marca el onboarding como completado
     */
    fun markOnboardingAsCompleted() {
        hasSeenOnboarding = true
    }
    
    /**
     * Reinicia el onboarding (para pruebas o desde settings)
     */
    fun resetOnboarding() {
        hasSeenOnboarding = false
    }
}
