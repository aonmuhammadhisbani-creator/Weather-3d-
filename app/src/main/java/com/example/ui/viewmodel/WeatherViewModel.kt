package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CityEntity
import com.example.data.model.GeocodingResultDto
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherResponse
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository.create(application)

    val savedCities: StateFlow<List<CityEntity>> = repository.allCities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCity: StateFlow<CityEntity?> = repository.selectedCityFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isFahrenheit = MutableStateFlow(false)
    val isFahrenheit: StateFlow<Boolean> = _isFahrenheit.asStateFlow()

    private val _selectedHourIndex = MutableStateFlow(0)
    val selectedHourIndex: StateFlow<Int> = _selectedHourIndex.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeocodingResultDto>>(emptyList())
    val searchResults: StateFlow<List<GeocodingResultDto>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeDefaultCitiesIfNeeded()
        }

        viewModelScope.launch {
            selectedCity.collectLatest { city ->
                if (city != null) {
                    loadWeatherForCity(city, forceRefresh = false)
                }
            }
        }
    }

    fun loadWeatherForCity(city: CityEntity, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedHourIndex.value = 0

            val result = repository.fetchWeather(city, forceRefresh)
            result.onSuccess { response ->
                _weather.value = response
            }.onFailure { error ->
                _errorMessage.value = error.localizedMessage ?: "Failed to load forecast"
            }
            _isLoading.value = false
        }
    }

    fun refreshWeather() {
        val city = selectedCity.value ?: return
        loadWeatherForCity(city, forceRefresh = true)
    }

    fun selectCity(cityId: Long) {
        viewModelScope.launch {
            repository.selectCity(cityId)
        }
    }

    fun addCityFromGeocoding(result: GeocodingResultDto) {
        viewModelScope.launch {
            val city = CityEntity(
                id = result.id,
                name = result.name,
                country = result.country ?: "",
                adminArea = result.adminArea,
                latitude = result.latitude,
                longitude = result.longitude,
                timezone = result.timezone ?: "auto",
                isFavorite = true,
                isSelected = true
            )
            repository.addCity(city)
        }
    }

    fun deleteCity(cityId: Long) {
        viewModelScope.launch {
            repository.removeCity(cityId)
        }
    }

    fun toggleFavorite(city: CityEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(city)
        }
    }

    fun toggleTemperatureUnit() {
        _isFahrenheit.value = !_isFahrenheit.value
    }

    fun selectHourlyForecast(index: Int) {
        _selectedHourIndex.value = index
    }

    fun searchLocations(query: String) {
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(350) // debounce
            val results = repository.searchCities(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun getCurrentCondition(): WeatherCondition {
        val currentWeather = _weather.value?.current
        if (currentWeather != null) {
            val hourIdx = _selectedHourIndex.value
            if (hourIdx > 0) {
                val hourCode = _weather.value?.hourly?.weatherCode?.getOrNull(hourIdx) ?: currentWeather.weatherCode
                val isDay = if (hourIdx in 6..19) 1 else 0
                return WeatherCondition.fromWmoCode(hourCode, isDay)
            }
            return WeatherCondition.fromWmoCode(currentWeather.weatherCode, currentWeather.isDay)
        }
        return WeatherCondition.CLEAR_DAY
    }
}
