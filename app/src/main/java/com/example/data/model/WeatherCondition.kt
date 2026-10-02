package com.example.data.model

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class WeatherCondition(
    val label: String,
    val description: String,
    val isDaytime: Boolean,
    val iconEmoji: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val ambientSoundsAvailable: Boolean
) {
    CLEAR_DAY(
        label = "Sunny & Clear",
        description = "Radiant golden sunshine with clear atmospheric horizons.",
        isDaytime = true,
        iconEmoji = "☀️",
        primaryColor = Color(0xFFFFB300),
        secondaryColor = Color(0xFFFF7043),
        ambientSoundsAvailable = false
    ),
    CLEAR_NIGHT(
        label = "Starry Night",
        description = "Luminous moonlit sky with crystalline twinkling stars.",
        isDaytime = false,
        iconEmoji = "🌙",
        primaryColor = Color(0xFF64B5F6),
        secondaryColor = Color(0xFF1E88E5),
        ambientSoundsAvailable = false
    ),
    PARTLY_CLOUDY_DAY(
        label = "Partly Cloudy",
        description = "Gentle floating cumulus clouds passing through warm sunlight.",
        isDaytime = true,
        iconEmoji = "⛅",
        primaryColor = Color(0xFFFFCA28),
        secondaryColor = Color(0xFF78909C),
        ambientSoundsAvailable = false
    ),
    PARTLY_CLOUDY_NIGHT(
        label = "Partly Cloudy Night",
        description = "Soft silver clouds drifting across the lunar glow.",
        isDaytime = false,
        iconEmoji = "☁️",
        primaryColor = Color(0xFF90A4AE),
        secondaryColor = Color(0xFF37474F),
        ambientSoundsAvailable = false
    ),
    OVERCAST(
        label = "Overcast Clouds",
        description = "Dense volumetric cloud canopy spanning across the skyline.",
        isDaytime = true,
        iconEmoji = "☁️",
        primaryColor = Color(0xFF78909C),
        secondaryColor = Color(0xFF455A64),
        ambientSoundsAvailable = false
    ),
    FOG(
        label = "Misty Fog",
        description = "Rolling low-altitude vapor ribbons reducing ground visibility.",
        isDaytime = true,
        iconEmoji = "🌫️",
        primaryColor = Color(0xFFB0BEC5),
        secondaryColor = Color(0xFF546E7A),
        ambientSoundsAvailable = false
    ),
    LIGHT_RAIN(
        label = "Gentle Showers",
        description = "Mild refreshing precipitation and soft atmospheric mist.",
        isDaytime = true,
        iconEmoji = "🌦️",
        primaryColor = Color(0xFF4FC3F7),
        secondaryColor = Color(0xFF0288D1),
        ambientSoundsAvailable = true
    ),
    HEAVY_RAIN(
        label = "Heavy Downpour",
        description = "Torrential rain streaks hitting the surface with expanding ripples.",
        isDaytime = false,
        iconEmoji = "🌧️",
        primaryColor = Color(0xFF29B6F6),
        secondaryColor = Color(0xFF01579B),
        ambientSoundsAvailable = true
    ),
    THUNDERSTORM(
        label = "Thunderstorm",
        description = "Electric lightning arcs striking across volumetric storm clouds.",
        isDaytime = false,
        iconEmoji = "⛈️",
        primaryColor = Color(0xFFBA68C8),
        secondaryColor = Color(0xFF4A148C),
        ambientSoundsAvailable = true
    ),
    SNOW(
        label = "Blizzard & Snow",
        description = "Crystalline 3D snowflakes tumbling with gentle winter drafts.",
        isDaytime = true,
        iconEmoji = "❄️",
        primaryColor = Color(0xFF81D4FA),
        secondaryColor = Color(0xFFE1F5FE),
        ambientSoundsAvailable = true
    ),
    WINDY(
        label = "High Winds",
        description = "Vigorous atmospheric streamlines and turbulent wind vortexes.",
        isDaytime = true,
        iconEmoji = "💨",
        primaryColor = Color(0xFF80CBC4),
        secondaryColor = Color(0xFF00695C),
        ambientSoundsAvailable = true
    );

    val backgroundGradient: Brush
        get() = when (this) {
            CLEAR_DAY -> Brush.verticalGradient(
                listOf(Color(0xFF0277BD), Color(0xFF29B6F6), Color(0xFFFFB74D))
            )
            CLEAR_NIGHT -> Brush.verticalGradient(
                listOf(Color(0xFF050B14), Color(0xFF0D1B2A), Color(0xFF1B263B))
            )
            PARTLY_CLOUDY_DAY -> Brush.verticalGradient(
                listOf(Color(0xFF1E88E5), Color(0xFF64B5F6), Color(0xFFCFD8DC))
            )
            PARTLY_CLOUDY_NIGHT -> Brush.verticalGradient(
                listOf(Color(0xFF0B1325), Color(0xFF1C2541), Color(0xFF3A506B))
            )
            OVERCAST -> Brush.verticalGradient(
                listOf(Color(0xFF263238), Color(0xFF37474F), Color(0xFF546E7A))
            )
            FOG -> Brush.verticalGradient(
                listOf(Color(0xFF37474F), Color(0xFF607D8B), Color(0xFF90A4AE))
            )
            LIGHT_RAIN -> Brush.verticalGradient(
                listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF455A64))
            )
            HEAVY_RAIN -> Brush.verticalGradient(
                listOf(Color(0xFF0D1B2A), Color(0xFF1B263B), Color(0xFF203A43))
            )
            THUNDERSTORM -> Brush.verticalGradient(
                listOf(Color(0xFF120E24), Color(0xFF241442), Color(0xFF191D32))
            )
            SNOW -> Brush.verticalGradient(
                listOf(Color(0xFF1A365D), Color(0xFF2B6CB0), Color(0xFF90CDF4))
            )
            WINDY -> Brush.verticalGradient(
                listOf(Color(0xFF134E5E), Color(0xFF2B5876), Color(0xFF4E6572))
            )
        }

    companion object {
        fun fromWmoCode(code: Int, isDay: Int = 1): WeatherCondition {
            val day = isDay != 0
            return when (code) {
                0 -> if (day) CLEAR_DAY else CLEAR_NIGHT
                1, 2 -> if (day) PARTLY_CLOUDY_DAY else PARTLY_CLOUDY_NIGHT
                3 -> OVERCAST
                45, 48 -> FOG
                51, 53, 55, 61 -> LIGHT_RAIN
                63, 65, 80, 81, 82 -> HEAVY_RAIN
                71, 73, 75, 77, 85, 86 -> SNOW
                95, 96, 99 -> THUNDERSTORM
                else -> if (day) PARTLY_CLOUDY_DAY else PARTLY_CLOUDY_NIGHT
            }
        }
    }
}
