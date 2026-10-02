package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyWeatherDto
import com.example.data.model.WeatherCondition
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ExtendedForecastView(
    daily: DailyWeatherDto?,
    isFahrenheit: Boolean,
    modifier: Modifier = Modifier
) {
    if (daily == null || daily.time.isEmpty()) return

    val daysCount = daily.time.size.coerceAtMost(7)

    // Calculate global weekly min and max for the relative temperature bars
    val weekMinC = daily.temperatureMin.take(daysCount).minOrNull() ?: 10.0
    val weekMaxC = daily.temperatureMax.take(daysCount).maxOrNull() ?: 30.0
    val weekRange = (weekMaxC - weekMinC).coerceAtLeast(1.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("extended_forecast_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "7-Day Extended Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            for (i in 0 until daysCount) {
                val rawDate = daily.time[i]
                val dayName = if (i == 0) "Today" else if (i == 1) "Tomorrow" else formatDayName(rawDate)
                val code = daily.weatherCode.getOrNull(i) ?: 0
                val condition = WeatherCondition.fromWmoCode(code, 1)
                val minC = daily.temperatureMin.getOrNull(i) ?: 15.0
                val maxC = daily.temperatureMax.getOrNull(i) ?: 25.0
                val rainProb = daily.precipitationProbabilityMax?.getOrNull(i) ?: 0

                val minDisplay = if (isFahrenheit) "${((minC * 9 / 5) + 32).toInt()}°" else "${minC.toInt()}°"
                val maxDisplay = if (isFahrenheit) "${((maxC * 9 / 5) + 32).toInt()}°" else "${maxC.toInt()}°"

                // Relative positions within the weekly range for the progress bar
                val startFraction = ((minC - weekMinC) / weekRange).toFloat().coerceIn(0f, 0.8f)
                val endFraction = ((maxC - weekMinC) / weekRange).toFloat().coerceIn(0.2f, 1f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("daily_forecast_row_$i"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Day name
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.width(82.dp)
                    )

                    // Condition emoji & rain chance
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.width(72.dp)
                    ) {
                        Text(text = condition.iconEmoji, fontSize = 20.sp)
                        if (rainProb > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$rainProb%",
                                fontSize = 11.sp,
                                color = Color(0xFF90CAF9),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Low temp label
                    Text(
                        text = minDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.width(36.dp)
                    )

                    // Visual Temperature Range Slider Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .padding(horizontal = 6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(endFraction)
                                .height(6.dp)
                                .padding(start = (startFraction * 100).dp.coerceAtLeast(0.dp))
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF64B5F6), Color(0xFFFFB74D), Color(0xFFFF7043))
                                    )
                                )
                        )
                    }

                    // High temp label
                    Text(
                        text = maxDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.width(36.dp)
                    )
                }

                if (i < daysCount - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

private fun formatDayName(rawDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(rawDate) ?: return rawDate
        val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        rawDate
    }
}
