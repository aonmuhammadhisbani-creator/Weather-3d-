package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM saved_cities ORDER BY orderIndex ASC, name ASC")
    fun getAllCities(): Flow<List<CityEntity>>

    @Query("SELECT * FROM saved_cities WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedCity(): CityEntity?

    @Query("SELECT * FROM saved_cities WHERE isSelected = 1 LIMIT 1")
    fun getSelectedCityFlow(): Flow<CityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<CityEntity>)

    @Update
    suspend fun updateCity(city: CityEntity)

    @Query("UPDATE saved_cities SET isSelected = 0")
    suspend fun clearSelection()

    @Query("UPDATE saved_cities SET isSelected = 1 WHERE id = :cityId")
    suspend fun selectCity(cityId: Long)

    @Query("DELETE FROM saved_cities WHERE id = :cityId")
    suspend fun deleteCityById(cityId: Long)

    @Query("SELECT COUNT(*) FROM saved_cities")
    suspend fun getCityCount(): Int
}

@Dao
interface ForecastDao {
    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId LIMIT 1")
    suspend fun getCachedWeather(cityId: Long): WeatherCacheEntity?

    @Query("SELECT * FROM weather_cache WHERE cityId = :cityId LIMIT 1")
    fun getCachedWeatherFlow(cityId: Long): Flow<WeatherCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCache(cache: WeatherCacheEntity)
}
