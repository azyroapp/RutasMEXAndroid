package com.azyroapp.rutasmex.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.azyroapp.rutasmex.MainActivityCompose
import com.azyroapp.rutasmex.R
import com.azyroapp.rutasmex.data.local.AppDatabase
import com.azyroapp.rutasmex.data.model.ActiveTripState
import com.azyroapp.rutasmex.data.model.DistanceCalculationMode
import com.azyroapp.rutasmex.data.model.Route
import com.azyroapp.rutasmex.data.model.RouteDistanceResult
import com.azyroapp.rutasmex.data.model.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Servicio en primer plano para seguimiento de viaje
 * 
 * Mantiene el tracking activo incluso cuando la app está en background
 */
class TripTrackingService : Service() {
    
    private val binder = TripTrackingBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private lateinit var database: AppDatabase
    private lateinit var notificationManager: NotificationManager
    
    // Estado del viaje activo
    private val _activeTripState = MutableStateFlow<ActiveTripState?>(null)
    val activeTripState: StateFlow<ActiveTripState?> = _activeTripState.asStateFlow()
    
    private var currentTrip: Trip? = null
    private var currentRoute: Route? = null
    private var calculationMode: DistanceCalculationMode = DistanceCalculationMode.IDA
    
    // Tracking de ubicación
    private val userPath = mutableListOf<Location>()
    private var lastLocation: Location? = null
    private var distanceTraveled = 0.0
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "trip_tracking_channel"
        private const val CHANNEL_NAME = "Seguimiento de Viaje"
        
        const val ACTION_START_TRIP = "com.azyroapp.rutasmex.START_TRIP"
        const val ACTION_STOP_TRIP = "com.azyroapp.rutasmex.STOP_TRIP"
        const val ACTION_CANCEL_TRIP = "com.azyroapp.rutasmex.CANCEL_TRIP"
        
        const val EXTRA_TRIP = "extra_trip"
        const val EXTRA_ROUTE = "extra_route"
        const val EXTRA_CALCULATION_MODE = "extra_calculation_mode"
        
        // Velocidad promedio de transporte público (km/h)
        private const val AVERAGE_SPEED_KMH = 25.0
    }
    
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(applicationContext)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRIP -> {
                val trip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_TRIP, Trip::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_TRIP)
                }
                
                val route = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_ROUTE, Route::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_ROUTE)
                }
                
                val mode = intent.getStringExtra(EXTRA_CALCULATION_MODE)?.let {
                    DistanceCalculationMode.valueOf(it)
                } ?: DistanceCalculationMode.IDA
                
                if (trip != null && route != null) {
                    startTrip(trip, route, mode)
                }
            }
            ACTION_STOP_TRIP -> {
                stopTrip(completed = true)
            }
            ACTION_CANCEL_TRIP -> {
                stopTrip(completed = false)
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
    
    /**
     * Inicia el seguimiento de un viaje
     */
    private fun startTrip(trip: Trip, route: Route, mode: DistanceCalculationMode) {
        currentTrip = trip
        currentRoute = route
        calculationMode = mode
        userPath.clear()
        lastLocation = null
        distanceTraveled = 0.0
        
        // Guardar viaje en base de datos
        serviceScope.launch {
            database.tripDao().insertTrip(trip)
        }
        
        // Iniciar servicio en primer plano
        val notification = createNotification(
            routeName = trip.routeName,
            distanceRemaining = trip.totalDistance,
            progress = 0f
        )
        startForeground(NOTIFICATION_ID, notification)
        
        // Actualizar estado
        updateActiveTripState(
            distanceToDestination = trip.totalDistance,
            estimatedTimeMinutes = calculateEstimatedTime(trip.totalDistance)
        )
    }
    
    /**
     * Detiene el seguimiento del viaje
     */
    private fun stopTrip(completed: Boolean) {
        val trip = currentTrip ?: return
        
        val endTime = Date()
        val duration = (endTime.time - trip.startTime.time) / 1000 // segundos
        
        val updatedTrip = trip.copy(
            endTime = endTime,
            isCompleted = completed,
            isCancelled = !completed,
            distanceTraveled = distanceTraveled,
            duration = duration,
            averageSpeed = if (duration > 0) {
                (distanceTraveled / 1000.0) / (duration / 3600.0) // km/h
            } else 0.0
        )
        
        // Actualizar en base de datos
        serviceScope.launch {
            database.tripDao().updateTrip(updatedTrip)
        }
        
        // Limpiar estado
        currentTrip = null
        currentRoute = null
        userPath.clear()
        _activeTripState.value = null
        
        // Detener servicio
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    /**
     * Actualiza la ubicación del usuario durante el viaje
     */
    fun updateLocation(
        location: Location,
        distanceResult: RouteDistanceResult?
    ) {
        val trip = currentTrip ?: return
        
        // Agregar ubicación al path
        userPath.add(location)
        
        // Calcular distancia recorrida
        lastLocation?.let { last ->
            distanceTraveled += last.distanceTo(location).toDouble()
        }
        lastLocation = location
        
        // Actualizar notificación y estado
        distanceResult?.let { result ->
            val progress = if (trip.totalDistance > 0) {
                (distanceTraveled / trip.totalDistance).toFloat().coerceIn(0f, 1f)
            } else 0f
            
            val estimatedTime = calculateEstimatedTime(result.distanceToDestination)
            
            // Actualizar notificación
            val notification = createNotification(
                routeName = trip.routeName,
                distanceRemaining = result.distanceToDestination,
                progress = progress
            )
            notificationManager.notify(NOTIFICATION_ID, notification)
            
            // Actualizar estado
            updateActiveTripState(
                distanceToDestination = result.distanceToDestination,
                estimatedTimeMinutes = estimatedTime
            )
            
            // Si llegó al destino, completar viaje
            if (result.distanceToDestination <= 50) { // 50 metros
                stopTrip(completed = true)
            }
        }
    }
    
    /**
     * Actualiza el estado del viaje activo
     */
    private fun updateActiveTripState(
        distanceToDestination: Double,
        estimatedTimeMinutes: Int
    ) {
        val trip = currentTrip ?: return
        
        val progress = if (trip.totalDistance > 0) {
            (distanceTraveled / trip.totalDistance).toFloat().coerceIn(0f, 1f)
        } else 0f
        
        _activeTripState.value = ActiveTripState(
            trip = trip.copy(distanceTraveled = distanceTraveled),
            currentDistanceToDestination = distanceToDestination,
            estimatedTimeMinutes = estimatedTimeMinutes,
            progress = progress
        )
    }
    
    /**
     * Calcula el tiempo estimado de llegada
     */
    private fun calculateEstimatedTime(distanceMeters: Double): Int {
        val distanceKm = distanceMeters / 1000.0
        val hours = distanceKm / AVERAGE_SPEED_KMH
        return (hours * 60).toInt().coerceAtLeast(1)
    }
    
    /**
     * Crea el canal de notificación
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones de seguimiento de viaje"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Crea la notificación persistente
     */
    private fun createNotification(
        routeName: String,
        distanceRemaining: Double,
        progress: Float
    ): Notification {
        val intent = Intent(this, MainActivityCompose::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val distanceText = if (distanceRemaining >= 1000) {
            String.format("%.2f km", distanceRemaining / 1000)
        } else {
            String.format("%.0f m", distanceRemaining)
        }
        
        val estimatedTime = calculateEstimatedTime(distanceRemaining)
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Viaje en $routeName")
            .setContentText("$distanceText restantes • ~$estimatedTime min")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setProgress(100, (progress * 100).toInt(), false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            .build()
    }
    
    /**
     * Binder para comunicación con el servicio
     */
    inner class TripTrackingBinder : Binder() {
        fun getService(): TripTrackingService = this@TripTrackingService
    }
}
