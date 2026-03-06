package com.azyroapp.rutasmex.data.model

/**
 * Modos de cálculo de distancia en rutas
 * 
 * - IDA: Calcula usando solo el segmento de IDA
 * - REGRESO: Calcula usando solo el segmento de REGRESO
 * - COMPLETO: Calcula usando IDA + REGRESO (ruta completa circular)
 */
enum class DistanceCalculationMode {
    IDA,
    REGRESO,
    COMPLETO;
    
    /**
     * Obtiene el siguiente modo en el ciclo
     */
    fun next(): DistanceCalculationMode {
        return when (this) {
            IDA -> REGRESO
            REGRESO -> COMPLETO
            COMPLETO -> IDA
        }
    }
    
    /**
     * Nombre para mostrar en UI
     */
    fun displayName(): String {
        return when (this) {
            IDA -> "IDA"
            REGRESO -> "REGRESO"
            COMPLETO -> "COMPLETO"
        }
    }
    
    /**
     * Color asociado al modo
     */
    fun color(): String {
        return when (this) {
            IDA -> "#00C3FF"      // Azul
            REGRESO -> "#FF6B00"  // Naranja
            COMPLETO -> "#9C27B0" // Morado
        }
    }
}
