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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyWeatherDto
import com.example.data.model.WeatherCondition

data class HourlyItemData(
    val timeLabel: String,
    val tempC: Double,
    val rainProb: Int,
    val condition: WeatherCondition,
    val isCurrentHour: Boolean
)

@Composable
fun HourlyForecastTimeline(
    hourly: HourlyWeatherDto?,
    isFahrenheit: Boolean,
    selectedHourIndex: Int,
    onHourSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (hourly == null || hourly.time.isEmpty()) return

    // Take the next 24 hours
    val count = hourly.time.size.coerceAtMost(24)
    val items = (0 until count).map { i ->
        val rawTime = hourly.time[i]
        val timeLabel = formatHourLabel(rawTime, i)
        val temp = hourly.temperature.getOrNull(i) ?: 20.0
        val rain = hourly.precipitationProbability?.getOrNull(i) ?: 0
        val code = hourly.weatherCode.getOrNull(i) ?: 0
        val condition = WeatherCondition.fromWmoCode(code, if (i in 6..19) 1 else 0)
        HourlyItemData(
            timeLabel = timeLabel,
            tempC = temp,
            rainProb = rain,
            condition = condition,
            isCurrentHour = i == 0
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hourly_forecast_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "24-Hour Hourly Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Tap to preview 3D",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    val isSelected = index == selectedHourIndex
                    val displayTemp = if (isFahrenheit) {
                        "${((item.tempC * 9 / 5) + 32).toInt()}°"
                    } else {
                        "${item.tempC.toInt()}°"
                    }

                    Column(
                        modifier = Modifier
                            .width(68.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSelected) Color.White.copy(alpha = 0.28f)
                                else Color.Black.copy(alpha = 0.20f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { onHourSelected(index) }
                            .padding(vertical = 12.dp, horizontal = 6.dp)
                            .testTag("hourly_item_$index"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (item.isCurrentHour) "Now" else item.timeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected || item.isCurrentHour) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.condition.iconEmoji,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = displayTemp,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        if (item.rainProb > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF64B5F6),
                                    modifier = Modifier.width(10.dp).height(10.dp)
                                )
                                Text(
                                    text = "${item.rainProb}%",
                                    fontSize = 10.sp,
                                    color = Color(0xFF90CAF9),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Text(
                                text = "-",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.35f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatHourLabel(rawTime: String, index: Int): String {
    return try {
        if (rawTime.contains("T")) {
            val hourPart = rawTime.substringAfter("T").take(5)
            hourPart
        } else {
            rawTime
        }
    } catch (e: Exception) {
        "+${index}h"
    }
}
