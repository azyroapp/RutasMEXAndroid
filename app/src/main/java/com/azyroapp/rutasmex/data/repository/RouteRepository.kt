package com.azyroapp.rutasmex.data.repository

import android.content.Context
import com.azyroapp.rutasmex.data.model.City
import com.azyroapp.rutasmex.data.model.CityData
import com.azyroapp.rutasmex.data.model.Route
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository para gestionar datos de ciudades y rutas
 */
class RouteRepository(private val context: Context) {
    
    private val gson = Gson()
    
    // Archivos JSON de las ciudades
    private val cityFiles = listOf(
        "tuxtla_mapkit.json",
        "sancristobal_mapkit.json",
        "comitan_mapkit.json",
        "tapachula_mapkit.json"
    )
    
    /**
     * Carga todas las ciudades disponibles
     */
    suspend fun loadCities(): Result<List<City>> = withContext(Dispatchers.IO) {
        try {
            val cities = mutableListOf<City>()
            
            cityFiles.forEach { fileName ->
                val cityData = loadCityDataFromFile(fileName)
                cityData?.let { cities.add(it.ciudad) }
            }
            
            Result.success(cities.sortedBy { it.name })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Carga las rutas de una ciudad específica
     */
    suspend fun loadRoutesForCity(cityId: String): Result<List<Route>> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${cityId}_mapkit.json"
            val cityData = loadCityDataFromFile(fileName)
            
            if (cityData != null) {
                Result.success(cityData.rutas.sortedBy { it.name })
            } else {
                Result.failure(IOException("No se encontró el archivo $fileName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Carga los datos completos de una ciudad (ciudad + rutas)
     */
    suspend fun loadCityData(cityId: String): Result<CityData> = withContext(Dispatchers.IO) {
        try {
            val fileName = if (cityId.endsWith(".json")) cityId else "${cityId}_mapkit.json"
            val cityData = loadCityDataFromFile(fileName)
            
            if (cityData != null) {
                Result.success(cityData)
            } else {
                Result.failure(IOException("No se encontró el archivo $fileName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Carga un archivo JSON desde assets
     */
    private fun loadCityDataFromFile(fileName: String): CityData? {
        return try {
            val json = context.assets.open("routes/$fileName").bufferedReader().use { it.readText() }
            gson.fromJson(json, CityData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
