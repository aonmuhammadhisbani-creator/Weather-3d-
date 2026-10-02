package com.example.ui.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentWeatherDto
import com.example.data.model.DailyWeatherDto
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherMetricCards(
    current: CurrentWeatherDto?,
    daily: DailyWeatherDto?,
    isFahrenheit: Boolean,
    modifier: Modifier = Modifier
) {
    if (current == null) return

    val windSpeed = if (isFahrenheit) {
        String.format("%.1f mph", current.windSpeed * 0.621371)
    } else {
        String.format("%.1f km/h", current.windSpeed)
    }

    val cardinalDir = getCardinalDirection(current.windDirection)
    val uvIndex = daily?.uvIndexMax?.firstOrNull() ?: 5.0
    val pressure = current.pressureMsl ?: current.surfacePressure ?: 1013.25
    val humidity = current.relativeHumidity

    val sunriseTime = daily?.sunrise?.firstOrNull()?.substringAfter("T")?.take(5) ?: "06:00"
    val sunsetTime = daily?.sunset?.firstOrNull()?.substringAfter("T")?.take(5) ?: "18:30"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_metrics_grid"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Atmospheric Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        // 2-Column Row 1: Wind Compass & UV Index
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Wind Card with Animated Compass Rose
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
                    .testTag("metric_card_wind"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WIND & GUSTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = windSpeed,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "$cardinalDir (${current.windDirection.toInt()}°)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        // 3D-styled animated compass dial
                        CompassDial(
                            degrees = current.windDirection.toFloat(),
                            modifier = Modifier.size(62.dp)
                        )
                    }

                    Text(
                        text = getWindStrengthAdvice(current.windSpeed),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }

            // UV Index Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(170.dp)
                    .testTag("metric_card_uv"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "UV INDEX",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column {
                        Text(
                            text = String.format("%.1f", uvIndex),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = getUvCategory(uvIndex),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = getUvColor(uvIndex)
                        )
                    }

                    // UV Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((uvIndex.toFloat() / 11f).coerceIn(0.05f, 1f))
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF81C784), Color(0xFFFFD54F), Color(0xFFFF7043), Color(0xFFBA68C8))
                                    )
                                )
                        )
                    }

                    Text(
                        text = getUvAdvice(uvIndex),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }
        }

        // 2-Column Row 2: Humidity & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Humidity Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .testTag("metric_card_humidity"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HUMIDITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "$humidity%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = if (humidity < 40) "Dry air" else if (humidity < 65) "Optimal comfort" else "High moisture",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            // Air Pressure Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .testTag("metric_card_pressure"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Compress,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AIR PRESSURE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "${pressure.toInt()} hPa",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = if (pressure > 1015) "High barometer (fair)" else "Normal atmospheric",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // Row 3: Sunrise & Sunset Solar Path Arc
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("metric_card_sun_path"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbTwilight,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SUNRISE & SUNSET",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "Dawn to Dusk",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Canvas drawing the sun's trajectory arc across horizon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Horizon baseline
                        drawLine(
                            color = Color.White.copy(alpha = 0.2f),
                            start = Offset(0f, h - 8f),
                            end = Offset(w, h - 8f),
                            strokeWidth = 1.5f
                        )

                        // Daylight arc curve
                        val arcPath = Path().apply {
                            moveTo(20f, h - 8f)
                            cubicTo(
                                w * 0.25f, -10f,
                                w * 0.75f, -10f,
                                w - 20f, h - 8f
                            )
                        }

                        drawPath(
                            path = arcPath,
                            color = Color.White.copy(alpha = 0.35f),
                            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                        )

                        // Current sun position on the arc (approximated midpoint ~ noon)
                        val sunX = w * 0.52f
                        val sunY = 12f

                        // Sun ray aura
                        drawCircle(
                            color = Color(0xFFFFEE58).copy(alpha = 0.35f),
                            radius = 16f,
                            center = Offset(sunX, sunY)
                        )
                        drawCircle(
                            color = Color(0xFFFFB300),
                            radius = 8f,
                            center = Offset(sunX, sunY)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Sunrise",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = sunriseTime,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Sunset",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = sunsetTime,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompassDial(
    degrees: Float,
    modifier: Modifier = Modifier
) {
    val animDegrees = remember { Animatable(degrees) }
    LaunchedEffect(degrees) {
        animDegrees.animateTo(degrees, animationSpec = tween(600))
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f - 4f

        // Outer ring
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )

        // North indicator tick
        drawCircle(
            color = Color(0xFFEF5350),
            radius = 3f,
            center = Offset(center.x, center.y - radius + 5f)
        )

        // Pointer Needle pointing in direction
        val rad = (animDegrees.value - 90f) * (PI.toFloat() / 180f)
        val needleLen = radius * 0.75f
        val tipX = center.x + cos(rad) * needleLen
        val tipY = center.y + sin(rad) * needleLen

        val oppRad = rad + PI.toFloat()
        val tailX = center.x + cos(oppRad) * (needleLen * 0.45f)
        val tailY = center.y + sin(oppRad) * (needleLen * 0.45f)

        // Red North/Direction needle
        drawLine(
            color = Color(0xFFEF5350),
            start = center,
            end = Offset(tipX, tipY),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )

        // Silver Tail
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = center,
            end = Offset(tailX, tailY),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )

        // Center pivot
        drawCircle(color = Color.White, radius = 3.5f, center = center)
    }
}

private fun getCardinalDirection(degrees: Double): String {
    val dirs = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = (((degrees % 360) / 22.5) + 0.5).toInt() % 16
    return dirs[index]
}

private fun getWindStrengthAdvice(kmh: Double): String = when {
    kmh < 12 -> "Calm / gentle breeze"
    kmh < 29 -> "Moderate breeze"
    kmh < 50 -> "Fresh strong breeze"
    else -> "High wind alert"
}

private fun getUvCategory(uv: Double): String = when {
    uv < 3 -> "Low"
    uv < 6 -> "Moderate"
    uv < 8 -> "High"
    uv < 11 -> "Very High"
    else -> "Extreme"
}

private fun getUvColor(uv: Double): Color = when {
    uv < 3 -> Color(0xFF81C784)
    uv < 6 -> Color(0xFFFFD54F)
    uv < 8 -> Color(0xFFFFB74D)
    uv < 11 -> Color(0xFFFF7043)
    else -> Color(0xFFBA68C8)
}

private fun getUvAdvice(uv: Double): String = when {
    uv < 3 -> "No special protection needed"
    uv < 6 -> "Seek shade during peak midday"
    uv < 8 -> "Wear sunscreen SPF 30+ & hat"
    else -> "Avoid prolonged direct exposure"
}
