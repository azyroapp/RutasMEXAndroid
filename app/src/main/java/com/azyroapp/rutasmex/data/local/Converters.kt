package com.azyroapp.rutasmex.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Conversores de tipos para Room
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
