package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val cityId: Long,
    val jsonPayload: String,
    val conditionName: String,
    val temperatureC: Double,
    val highTempC: Double,
    val lowTempC: Double,
    val lastUpdatedMillis: Long
)
