package com.example.ui.studio3d

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherCondition
import com.example.ui.audio.WeatherAudioSynthesizer
import com.example.ui.engine3d.Weather3DStage

@Composable
fun WeatherStudioScreen(
    currentCondition: WeatherCondition,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCondition by remember { mutableStateOf(currentCondition) }
    var autoSpin by remember { mutableStateOf(true) }
    var windSpeedSlider by remember { mutableFloatStateOf(24f) }
    var timeOfDaySlider by remember { mutableFloatStateOf(0.5f) } // 0.0 to 1.0
    var isAudioActive by remember { mutableStateOf(false) }
    var showControlPanel by remember { mutableStateOf(true) }

    val audioSynthesizer = remember { WeatherAudioSynthesizer() }

    DisposableEffect(selectedCondition, isAudioActive) {
        if (isAudioActive) {
            audioSynthesizer.startAmbient(selectedCondition)
        } else {
            audioSynthesizer.stopAmbient()
        }
        onDispose {
            audioSynthesizer.stopAmbient()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(selectedCondition.backgroundGradient)
            .testTag("weather_studio_screen")
    ) {
        // Full screen 3D Weather Model Stage
        Weather3DStage(
            condition = selectedCondition,
            modifier = Modifier.fillMaxSize(),
            windSpeedKmh = windSpeedSlider.toDouble(),
            timeOfDayProgress = timeOfDaySlider,
            autoSpin = autoSpin,
            showControls = true
        )

        // Top Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "3D Weather Stage",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Interactive 3D model simulation",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sound toggle button
                IconButton(
                    onClick = { isAudioActive = !isAudioActive },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isAudioActive) Color(0xFF64B5F6) else Color.Black.copy(alpha = 0.35f))
                        .testTag("toggle_ambient_sound")
                ) {
                    Icon(
                        imageVector = if (isAudioActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Ambient Audio",
                        tint = if (isAudioActive) Color.Black else Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Toggle settings drawer
                IconButton(
                    onClick = { showControlPanel = !showControlPanel },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .testTag("toggle_control_panel")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Controls",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Controls Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 76.dp) // space for bottom navigation
        ) {
            // Condition Selector Chips Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(WeatherCondition.values()) { condition ->
                    val isSelected = condition == selectedCondition
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCondition = condition },
                        label = {
                            Text(
                                text = "${condition.iconEmoji} ${condition.label}",
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.White,
                            containerColor = Color.Black.copy(alpha = 0.45f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.White.copy(alpha = 0.4f),
                            selectedBorderColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Condition Info & Simulation Sliders Card
            AnimatedVisibility(visible = showControlPanel) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.55f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedCondition.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = selectedCondition.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            // Auto Orbit Switch
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp)
                            ) {
                                Text(
                                    text = "Auto-Orbit",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = autoSpin,
                                    onCheckedChange = { autoSpin = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF64B5F6),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier.testTag("switch_auto_orbit")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Wind Speed Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Wind Velocity: ${windSpeedSlider.toInt()} km/h",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.width(160.dp)
                            )
                            Slider(
                                value = windSpeedSlider,
                                onValueChange = { windSpeedSlider = it },
                                valueRange = 5f..80f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF64B5F6),
                                    activeTrackColor = Color(0xFF64B5F6),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }

                        // Time of Day Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val timeLabel = when {
                                timeOfDaySlider < 0.25f -> "Night"
                                timeOfDaySlider < 0.45f -> "Dawn / Morning"
                                timeOfDaySlider < 0.75f -> "Midday"
                                else -> "Dusk / Sunset"
                            }
                            Text(
                                text = "Lighting: $timeLabel",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                modifier = Modifier.width(160.dp)
                            )
                            Slider(
                                value = timeOfDaySlider,
                                onValueChange = { timeOfDaySlider = it },
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFFFB74D),
                                    activeTrackColor = Color(0xFFFFB74D),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
