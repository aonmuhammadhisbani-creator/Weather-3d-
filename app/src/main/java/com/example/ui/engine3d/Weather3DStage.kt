package com.example.ui.engine3d

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherCondition
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun Weather3DStage(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    landmark: FamousLandmark = FamousLandmark.GLOBAL_ICONIC_CITY,
    windSpeedKmh: Double = 15.0,
    timeOfDayProgress: Float = 0.5f, // 0.0 (midnight) to 0.5 (noon) to 1.0
    autoSpin: Boolean = true,
    showControls: Boolean = true,
    lightIntensity: Float = 1.0f
) {
    val scope = rememberCoroutineScope()
    val sceneState = remember { Weather3DSceneState() }

    // Pitch and Yaw angles
    var rotPitch by remember { mutableFloatStateOf(0.12f) }
    var rotYaw by remember { mutableFloatStateOf(0.0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Animation time tracker
    var animTime by remember { mutableFloatStateOf(0f) }

    // Frame clock for smooth 60fps physics simulation
    LaunchedEffect(condition) {
        var lastTimeNanos = 0L
        while (true) {
            withFrameNanos { nowNanos ->
                if (lastTimeNanos > 0L) {
                    val deltaSec = (nowNanos - lastTimeNanos) / 1_000_000_000f
                    animTime += deltaSec

                    // Auto orbit yaw when not dragging
                    if (autoSpin && !isDragging) {
                        rotYaw = (rotYaw + deltaSec * 0.45f) % (2f * PI.toFloat())
                        // Gentle breathing pitch
                        rotPitch = 0.12f + sin(animTime * 0.8f) * 0.08f
                    }

                    // Update physics
                    val windFactor = (windSpeedKmh.toFloat() / 50f).coerceIn(0.1f, 2.5f)
                    Condition3DModels.updatePhysics(sceneState, deltaSec, condition, windFactor)
                }
                lastTimeNanos = nowNanos
            }
        }
    }

    Box(
        modifier = modifier
            .testTag("weather_3d_stage")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Drag rotates model in 3D
                        val sensitivity = 0.008f
                        rotYaw += dragAmount.x * sensitivity
                        rotPitch = (rotPitch - dragAmount.y * sensitivity).coerceIn(-0.95f, 0.95f)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Reset to front view
                        rotPitch = 0.12f
                        rotYaw = 0.0f
                    }
                )
            }
    ) {
        // The 3D Rendering Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            Condition3DModels.render(
                drawScope = this,
                condition = condition,
                landmark = landmark,
                state = sceneState,
                rotPitch = rotPitch,
                rotYaw = rotYaw,
                animTime = animTime,
                timeOfDayProgress = timeOfDayProgress,
                lightIntensity = lightIntensity
            )
        }

        // 3D HUD & Controls overlay (if enabled)
        if (showControls) {
            // Subtle 3D indicator chip at top-start
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "3D INTERACTIVE",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Famous Landmark Identification Badge at bottom-start
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.50f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("landmark_badge")
            ) {
                Text(
                    text = "${landmark.emoji} ${landmark.landmarkName} • ${landmark.countryName}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Quick reset camera button
            FilledIconButton(
                onClick = {
                    rotPitch = 0.12f
                    rotYaw = 0f
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .testTag("reset_3d_camera"),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.35f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset 3D View",
                    tint = Color.White
                )
            }
        }
    }
}
