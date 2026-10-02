package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class CityEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val country: String,
    val adminArea: String?,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val isFavorite: Boolean = false,
    val isSelected: Boolean = false,
    val orderIndex: Int = 0
)
