package com.azyroapp.rutasmex.data.model

import com.google.gson.annotations.SerializedName

/**
 * Punto geográfico con latitud y longitud
 */
data class GeoPoint(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double
)

/**
 * Límites geográficos de una región
 */
data class GeoBounds(
    @SerializedName("minLatitude")
    val minLatitude: Double,
    
    @SerializedName("maxLatitude")
    val maxLatitude: Double,
    
    @SerializedName("minLongitude")
    val minLongitude: Double,
    
    @SerializedName("maxLongitude")
    val maxLongitude: Double
)
