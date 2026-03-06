package com.azyroapp.rutasmex.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para una ciudad
 */
data class City(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("totalRoutes")
    val totalRoutes: Int,
    
    @SerializedName("bounds")
    val bounds: GeoBounds,
    
    @SerializedName("center")
    val center: GeoPoint
)
