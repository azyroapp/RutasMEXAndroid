package com.azyroapp.rutasmex.core.services

import android.content.Context
import android.content.Intent
import android.os.Build
import com.azyroapp.rutasmex.data.model.DistanceCalculationMode
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.data.model.Trip

/**
 * Helper para gestionar el TripTrackingService
 */
object TripTrackingHelper {
    
    /**
     * Inicia el servicio de tracking de viaje
     */
    fun startTripTracking(
        context: Context,
        trip: Trip,
        route: Route,
        calculationMode: DistanceCalculationMode
    ) {
        val intent = Intent(context, TripTrackingService::class.java).apply {
            action = TripTrackingService.ACTION_START_TRIP
            putExtra(TripTrackingService.EXTRA_TRIP, trip)
            putExtra(TripTrackingService.EXTRA_ROUTE, route)
            putExtra(TripTrackingService.EXTRA_CALCULATION_MODE, calculationMode.name)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
    
    /**
     * Detiene el servicio de tracking (viaje completado)
     */
    fun stopTripTracking(context: Context) {
        val intent = Intent(context, TripTrackingService::class.java).apply {
            action = TripTrackingService.ACTION_STOP_TRIP
        }
        context.startService(intent)
    }
    
    /**
     * Cancela el servicio de tracking (viaje cancelado)
     */
    fun cancelTripTracking(context: Context) {
        val intent = Intent(context, TripTrackingService::class.java).apply {
            action = TripTrackingService.ACTION_CANCEL_TRIP
        }
        context.startService(intent)
    }
}
