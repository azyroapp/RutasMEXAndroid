package com.azyroapp.rutasmex.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Punto geográfico con latitud y longitud
 */
@Parcelize
data class GeoPoint(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double
) : Parcelable

/**
 * Límites geográficos de una región
 */
@Parcelize
data class GeoBounds(
    @SerializedName("minLatitude")
    val minLatitude: Double,
    
    @SerializedName("maxLatitude")
    val maxLatitude: Double,
    
    @SerializedName("minLongitude")
    val minLongitude: Double,
    
    @SerializedName("maxLongitude")
    val maxLongitude: Double
) : Parcelable

