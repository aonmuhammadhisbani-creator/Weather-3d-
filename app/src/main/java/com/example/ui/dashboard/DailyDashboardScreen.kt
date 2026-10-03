package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherCondition
import com.example.ui.engine3d.FamousLandmark
import com.example.ui.engine3d.Weather3DStage
import com.example.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDashboardScreen(
    viewModel: WeatherViewModel,
    onNavigateTo3DStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCity by viewModel.selectedCity.collectAsState()
    val savedCities by viewModel.savedCities.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isFahrenheit by viewModel.isFahrenheit.collectAsState()
    val selectedHourIndex by viewModel.selectedHourIndex.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var showSearchSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentCondition = viewModel.getCurrentCondition()
    val current = weather?.current
    val daily = weather?.daily
    val landmark = FamousLandmark.forLocation(selectedCity?.country, selectedCity?.name)

    // Temperature calculations
    val currentTempC = if (selectedHourIndex > 0) {
        weather?.hourly?.temperature?.getOrNull(selectedHourIndex) ?: current?.temperature ?: 20.0
    } else {
        current?.temperature ?: 20.0
    }

    val feelsLikeC = current?.apparentTemperature ?: currentTempC
    val highTempC = daily?.temperatureMax?.firstOrNull() ?: currentTempC
    val lowTempC = daily?.temperatureMin?.firstOrNull() ?: currentTempC

    val displayTemp = if (isFahrenheit) "${((currentTempC * 9 / 5) + 32).toInt()}°" else "${currentTempC.toInt()}°"
    val displayFeelsLike = if (isFahrenheit) "${((feelsLikeC * 9 / 5) + 32).toInt()}°" else "${feelsLikeC.toInt()}°"
    val displayHigh = if (isFahrenheit) "${((highTempC * 9 / 5) + 32).toInt()}°" else "${highTempC.toInt()}°"
    val displayLow = if (isFahrenheit) "${((lowTempC * 9 / 5) + 32).toInt()}°" else "${lowTempC.toInt()}°"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(currentCondition.backgroundGradient)
            .testTag("daily_dashboard_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar: Location, Search, Unit Toggle, Refresh
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location button (opens search sheet)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { showSearchSheet = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("location_selector_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = selectedCity?.name ?: "Select City",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (!selectedCity?.country.isNullOrBlank()) {
                                Text(
                                    text = selectedCity?.country ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    // Action buttons: °C/°F toggle, search, refresh
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Temperature Unit Switcher Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { viewModel.toggleTemperatureUnit() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("unit_toggle_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isFahrenheit) "°F" else "°C",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = { showSearchSheet = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("search_icon_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search City",
                                tint = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refreshWeather() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .testTag("refresh_weather_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Quick City Selector Pills
            if (savedCities.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedCities) { city ->
                            val isSelected = city.id == selectedCity?.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) Color.White.copy(alpha = 0.3f)
                                        else Color.Black.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.dp else 0.5.dp,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.selectCity(city.id) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("quick_city_${city.name}")
                            ) {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // HERO 3D WEATHER CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("hero_weather_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.14f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 3D Weather Stage Viewport (embedded with interactive drag & rotate!)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.15f))
                        ) {
                            Weather3DStage(
                                condition = currentCondition,
                                landmark = landmark,
                                modifier = Modifier.fillMaxSize(),
                                windSpeedKmh = current?.windSpeed ?: 15.0,
                                autoSpin = true,
                                showControls = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Temperature & Condition
                        Text(
                            text = displayTemp,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White,
                            lineHeight = 64.sp
                        )

                        Text(
                            text = currentCondition.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Feels like, High/Low
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Feels like $displayFeelsLike",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "•",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "H: $displayHigh  L: $displayLow",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Famous Landmark Info Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("hero_landmark_pill")
                        ) {
                            Text(
                                text = "${landmark.emoji} ${landmark.landmarkName} (${landmark.countryName})",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive 3D Watcher Studio Button
                        Button(
                            onClick = onNavigateTo3DStudio,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("launch_3d_studio_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Watch 3D Weather Models",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 24-HOUR HOURLY TIMELINE
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    HourlyForecastTimeline(
                        hourly = weather?.hourly,
                        isFahrenheit = isFahrenheit,
                        selectedHourIndex = selectedHourIndex,
                        onHourSelected = { viewModel.selectHourlyForecast(it) }
                    )
                }
            }

            // 7-DAY EXTENDED FORECAST
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ExtendedForecastView(
                        daily = weather?.daily,
                        isFahrenheit = isFahrenheit
                    )
                }
            }

            // ATMOSPHERIC METRIC CARDS
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    WeatherMetricCards(
                        current = current,
                        daily = daily,
                        isFahrenheit = isFahrenheit
                    )
                }
            }
        }

        // City Search Bottom Sheet
        if (showSearchSheet) {
            CitySearchSheet(
                sheetState = sheetState,
                savedCities = savedCities,
                selectedCity = selectedCity,
                searchResults = searchResults,
                isSearching = isSearching,
                onSearchQueryChanged = { viewModel.searchLocations(it) },
                onCitySelected = { viewModel.selectCity(it.id) },
                onGeocodingResultSelected = { viewModel.addCityFromGeocoding(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onDeleteCity = { viewModel.deleteCity(it) },
                onDismiss = { showSearchSheet = false }
            )
        }
    }
}
