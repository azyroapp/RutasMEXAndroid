package com.azyroapp.rutasmex.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que contiene una ciudad y sus rutas
 * Este es el formato del JSON que cargamos desde assets
 */
data class CityData(
    @SerializedName("ciudad")
    val ciudad: City,
    
    @SerializedName("rutas")
    val rutas: List<Route>
)
