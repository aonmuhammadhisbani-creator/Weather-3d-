package com.example.ui.engine3d

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.model.WeatherCondition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural 3D weather particle and model elements.
 */
class Weather3DSceneState {
    // Starfield particles
    val stars = List(140) {
        val r = 160f + Random.nextFloat() * 180f
        val theta = Random.nextFloat() * 2f * PI.toFloat()
        val phi = (Random.nextFloat() - 0.5f) * PI.toFloat()
        val x = r * cos(phi) * cos(theta)
        val y = r * sin(phi)
        val z = r * cos(phi) * sin(theta)
        StarParticle(Vector3(x, y, z), size = 1.2f + Random.nextFloat() * 2.2f, phase = Random.nextFloat() * 6.28f)
    }

    // Shooting stars
    var shootingStarProgress = 0f
    var shootingStarActive = false
    var shootingStarStart = Vector3(-180f, -120f, -80f)
    var shootingStarEnd = Vector3(180f, 60f, 60f)

    // Rain particles
    val rainDrops = List(160) {
        RainDrop(
            x = (Random.nextFloat() - 0.5f) * 360f,
            y = -220f + Random.nextFloat() * 440f,
            z = (Random.nextFloat() - 0.5f) * 300f,
            length = 24f + Random.nextFloat() * 28f,
            speed = 380f + Random.nextFloat() * 220f
        )
    }

    // Ground ripple rings from rain hits
    val rainRipples = MutableList(18) {
        RainRipple(
            x = (Random.nextFloat() - 0.5f) * 320f,
            z = (Random.nextFloat() - 0.5f) * 260f,
            radius = Random.nextFloat() * 30f,
            alpha = Random.nextFloat()
        )
    }

    // Snowflakes with 3D rotation angles
    val snowFlakes = List(130) {
        SnowParticle(
            x = (Random.nextFloat() - 0.5f) * 360f,
            y = -220f + Random.nextFloat() * 440f,
            z = (Random.nextFloat() - 0.5f) * 280f,
            radius = 2.5f + Random.nextFloat() * 4f,
            fallSpeed = 45f + Random.nextFloat() * 55f,
            driftSpeed = 20f + Random.nextFloat() * 30f,
            rotSpeedX = (Random.nextFloat() - 0.5f) * 3f,
            rotSpeedY = (Random.nextFloat() - 0.5f) * 3f,
            angleX = Random.nextFloat() * 6.28f,
            angleY = Random.nextFloat() * 6.28f
        )
    }

    // Cloud clusters: 3D spheres forming a dense sculpted cloud
    val cloudPuffs = listOf(
        CloudPuff(Vector3(-70f, -15f, -30f), 48f, 0.95f),
        CloudPuff(Vector3(-40f, -35f, -10f), 55f, 1.0f),
        CloudPuff(Vector3(0f, -42f, 0f), 65f, 1.0f),
        CloudPuff(Vector3(45f, -30f, -15f), 58f, 0.95f),
        CloudPuff(Vector3(75f, -10f, -25f), 50f, 0.9f),
        CloudPuff(Vector3(-25f, 0f, 25f), 52f, 0.92f),
        CloudPuff(Vector3(25f, 5f, 20f), 54f, 0.92f),
        CloudPuff(Vector3(60f, 15f, 10f), 42f, 0.85f),
        CloudPuff(Vector3(-60f, 10f, 15f), 40f, 0.85f),
        CloudPuff(Vector3(0f, 15f, 30f), 46f, 0.95f)
    )

    // Lightning bolt points in 3D
    var lightningActive = false
    var lightningIntensity = 0f
    var lightningSegments: List<Pair<Vector3, Vector3>> = emptyList()

    // Wind streamline particles
    val windStreamlines = List(12) { i ->
        WindStreamline(
            radius = 70f + i * 14f,
            height = -80f + i * 16f,
            speed = 1.2f + (i % 3) * 0.4f,
            lengthAngle = 1.2f + (i % 4) * 0.3f,
            phase = i * 0.55f
        )
    }
}

data class StarParticle(val pos: Vector3, val size: Float, val phase: Float)
data class RainDrop(var x: Float, var y: Float, var z: Float, val length: Float, val speed: Float)
data class RainRipple(var x: Float, var z: Float, var radius: Float, var alpha: Float)
data class SnowParticle(
    var x: Float, var y: Float, var z: Float,
    val radius: Float,
    val fallSpeed: Float,
    val driftSpeed: Float,
    val rotSpeedX: Float,
    val rotSpeedY: Float,
    var angleX: Float,
    var angleY: Float
)
data class CloudPuff(val center: Vector3, val radius: Float, val opacity: Float)
data class WindStreamline(val radius: Float, val height: Float, val speed: Float, val lengthAngle: Float, val phase: Float)

object Condition3DModels {

    fun updatePhysics(
        state: Weather3DSceneState,
        deltaSeconds: Float,
        condition: WeatherCondition,
        windFactor: Float
    ) {
        val dt = deltaSeconds.coerceIn(0.001f, 0.1f)

        // Update rain
        if (condition == WeatherCondition.LIGHT_RAIN || condition == WeatherCondition.HEAVY_RAIN || condition == WeatherCondition.THUNDERSTORM) {
            val speedMul = if (condition == WeatherCondition.HEAVY_RAIN || condition == WeatherCondition.THUNDERSTORM) 1.5f else 1.0f
            val windSlant = windFactor * 45f

            state.rainDrops.forEach { drop ->
                drop.y += drop.speed * speedMul * dt
                drop.x += windSlant * dt
                if (drop.y > 140f) {
                    drop.y = -180f - Random.nextFloat() * 60f
                    drop.x = (Random.nextFloat() - 0.5f) * 360f
                    drop.z = (Random.nextFloat() - 0.5f) * 300f
                }
            }

            // Update ripples
            state.rainRipples.forEach { ripple ->
                ripple.radius += 55f * dt
                ripple.alpha -= 1.8f * dt
                if (ripple.alpha <= 0f) {
                    ripple.alpha = 0.8f + Random.nextFloat() * 0.2f
                    ripple.radius = 2f
                    ripple.x = (Random.nextFloat() - 0.5f) * 300f
                    ripple.z = (Random.nextFloat() - 0.5f) * 240f
                }
            }
        }

        // Update snow
        if (condition == WeatherCondition.SNOW) {
            val windDrift = windFactor * 30f
            state.snowFlakes.forEach { flake ->
                flake.y += flake.fallSpeed * dt
                flake.x += (windDrift + sin(flake.angleY) * flake.driftSpeed) * dt
                flake.angleX += flake.rotSpeedX * dt
                flake.angleY += flake.rotSpeedY * dt

                if (flake.y > 140f) {
                    flake.y = -180f - Random.nextFloat() * 40f
                    flake.x = (Random.nextFloat() - 0.5f) * 360f
                    flake.z = (Random.nextFloat() - 0.5f) * 280f
                }
            }
        }

        // Update lightning trigger in thunderstorm
        if (condition == WeatherCondition.THUNDERSTORM) {
            if (state.lightningActive) {
                state.lightningIntensity -= dt * 3.8f
                if (state.lightningIntensity <= 0f) {
                    state.lightningActive = false
                    state.lightningSegments = emptyList()
                }
            } else {
                // Occasional lightning flash chance
                if (Random.nextFloat() < 0.025f) {
                    state.lightningActive = true
                    state.lightningIntensity = 1f
                    state.lightningSegments = generateLightningBolt()
                }
            }
        }

        // Update shooting star in clear night
        if (condition == WeatherCondition.CLEAR_NIGHT) {
            if (state.shootingStarActive) {
                state.shootingStarProgress += dt * 2.2f
                if (state.shootingStarProgress >= 1f) {
                    state.shootingStarActive = false
                }
            } else {
                if (Random.nextFloat() < 0.012f) {
                    state.shootingStarActive = true
                    state.shootingStarProgress = 0f
                    val startX = -180f + Random.nextFloat() * 100f
                    val startY = -140f + Random.nextFloat() * 60f
                    state.shootingStarStart = Vector3(startX, startY, -100f)
                    state.shootingStarEnd = Vector3(startX + 220f, startY + 120f, 60f)
                }
            }
        }
    }

    private fun generateLightningBolt(): List<Pair<Vector3, Vector3>> {
        val segments = mutableListOf<Pair<Vector3, Vector3>>()
        var curr = Vector3((Random.nextFloat() - 0.5f) * 60f, -80f, (Random.nextFloat() - 0.5f) * 40f)
        val targetY = 120f

        while (curr.y < targetY) {
            val nextY = curr.y + 18f + Random.nextFloat() * 22f
            val nextX = curr.x + (Random.nextFloat() - 0.5f) * 44f
            val nextZ = curr.z + (Random.nextFloat() - 0.5f) * 30f
            val next = Vector3(nextX, nextY.coerceAtMost(targetY), nextZ)
            segments.add(Pair(curr, next))

            // Branch chance
            if (Random.nextFloat() < 0.35f && segments.size < 12) {
                val branchEnd = Vector3(
                    curr.x + (Random.nextFloat() - 0.5f) * 70f,
                    curr.y + 35f,
                    curr.z + (Random.nextFloat() - 0.5f) * 40f
                )
                segments.add(Pair(curr, branchEnd))
            }
            curr = next
        }
        return segments
    }

    /**
     * Render the 3D model for the given condition onto the DrawScope canvas.
     */
    fun render(
        drawScope: DrawScope,
        condition: WeatherCondition,
        landmark: FamousLandmark = FamousLandmark.GLOBAL_ICONIC_CITY,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float,
        timeOfDayProgress: Float, // 0.0 (midnight) -> 0.5 (noon) -> 1.0
        lightIntensity: Float = 1.0f
    ) {
        val center = Offset(drawScope.size.width / 2f, drawScope.size.height / 2f)

        // Draw sky celestial bodies (Sun / Moon / Starfield)
        when (condition) {
            WeatherCondition.CLEAR_DAY -> {
                drawSun3D(drawScope, center + Offset(0f, -40f), rotPitch, rotYaw, animTime, lightIntensity, radiusScale = 0.9f)
            }
            WeatherCondition.CLEAR_NIGHT -> {
                drawMoonAndStars3D(drawScope, center + Offset(0f, -40f), state, rotPitch, rotYaw, animTime, moonScale = 0.9f)
            }
            WeatherCondition.PARTLY_CLOUDY_DAY -> {
                drawSun3D(drawScope, center + Offset(45f, -65f), rotPitch * 0.4f, rotYaw * 0.4f, animTime, 0.85f, radiusScale = 0.7f)
            }
            WeatherCondition.PARTLY_CLOUDY_NIGHT -> {
                drawMoonAndStars3D(drawScope, center + Offset(45f, -65f), state, rotPitch * 0.4f, rotYaw * 0.4f, animTime, moonScale = 0.7f)
            }
            else -> { /* Cloud decks / storms / fog */ }
        }

        // Draw the 3D Famous Landmark on the horizon
        Landmark3DModels.renderLandmark(
            drawScope = drawScope,
            landmark = landmark,
            center = center,
            rotPitch = rotPitch,
            rotYaw = rotYaw,
            condition = condition,
            animTime = animTime,
            lightningIntensity = state.lightningIntensity
        )

        // Draw weather atmospheric systems on top (clouds, rain, snow, lightning, fog, wind)
        when (condition) {
            WeatherCondition.CLEAR_DAY -> { /* pure sky & sun */ }
            WeatherCondition.CLEAR_NIGHT -> { /* pure night & moon */ }
            WeatherCondition.PARTLY_CLOUDY_DAY -> {
                drawClouds3D(drawScope, center + Offset(0f, -45f), state, rotPitch, rotYaw, animTime, isDark = false, hasSunlight = true)
            }
            WeatherCondition.PARTLY_CLOUDY_NIGHT -> {
                drawClouds3D(drawScope, center + Offset(0f, -45f), state, rotPitch, rotYaw, animTime, isDark = true, hasSunlight = false)
            }
            WeatherCondition.OVERCAST -> {
                drawClouds3D(drawScope, center + Offset(0f, -45f), state, rotPitch, rotYaw, animTime, isDark = true, hasSunlight = false)
            }
            WeatherCondition.FOG -> {
                drawFog3D(drawScope, center, rotPitch, rotYaw, animTime)
            }
            WeatherCondition.LIGHT_RAIN -> {
                drawClouds3D(drawScope, center + Offset(0f, -45f), state, rotPitch * 0.5f, rotYaw * 0.5f, animTime, isDark = false, hasSunlight = true)
                drawRain3D(drawScope, center, state, rotPitch, rotYaw, animTime, isHeavy = false)
            }
            WeatherCondition.HEAVY_RAIN -> {
                drawClouds3D(drawScope, center + Offset(0f, -45f), state, rotPitch * 0.5f, rotYaw * 0.5f, animTime, isDark = true, hasSunlight = false)
                drawRain3D(drawScope, center, state, rotPitch, rotYaw, animTime, isHeavy = true)
            }
            WeatherCondition.THUNDERSTORM -> {
                drawThunderstorm3D(drawScope, center, state, rotPitch, rotYaw, animTime)
            }
            WeatherCondition.SNOW -> {
                drawClouds3D(drawScope, center + Offset(0f, -45f), state, rotPitch * 0.5f, rotYaw * 0.5f, animTime, isDark = true, hasSunlight = false)
                drawSnow3D(drawScope, center, state, rotPitch, rotYaw, animTime)
            }
            WeatherCondition.WINDY -> {
                drawWind3D(drawScope, center, state, rotPitch, rotYaw, animTime)
            }
        }
    }

    private fun drawSun3D(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float,
        intensity: Float,
        radiusScale: Float = 1.0f
    ) {
        val baseRadius = 55f * radiusScale
        val pulse = 1f + 0.05f * sin(animTime * 2.5f)

        // 1. Draw Volumetric Sun Rays in 3D
        val numRays = 18
        for (i in 0 until numRays) {
            val angle = (i.toFloat() / numRays) * 2f * PI.toFloat() + animTime * 0.12f
            val rayLength = (baseRadius * 1.8f + (i % 3) * 16f) * pulse
            val rayVector = Vector3(cos(angle) * rayLength, sin(angle) * rayLength, 0f)
                .rotateX(rotPitch)
                .rotateY(rotYaw)

            val (projectedEnd, scale) = rayVector.project(400f, center)
            val rayColor = Color(0xFFFFC107).copy(alpha = (0.28f * intensity * scale).coerceIn(0.05f, 0.6f))

            scope.drawLine(
                color = rayColor,
                start = center,
                end = projectedEnd,
                strokeWidth = (4.5f * scale),
                cap = StrokeCap.Round
            )
        }

        // 2. Draw Orbiting 3D Corona Ellipses
        val coronaTilted1 = (0..32).map { idx ->
            val a = (idx / 32f) * 2f * PI.toFloat()
            val r = baseRadius * 1.35f
            Vector3(cos(a) * r, sin(a) * r * 0.35f, sin(a) * r * 0.6f)
                .rotateZ(animTime * 0.3f)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
        }
        draw3DLoop(scope, center, coronaTilted1, Color(0xFFFFD54F).copy(alpha = 0.45f * intensity), 2.5f)

        val coronaTilted2 = (0..32).map { idx ->
            val a = (idx / 32f) * 2f * PI.toFloat()
            val r = baseRadius * 1.5f
            Vector3(cos(a) * r * 0.45f, sin(a) * r, cos(a) * r * 0.7f)
                .rotateZ(-animTime * 0.25f)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
        }
        draw3DLoop(scope, center, coronaTilted2, Color(0xFFFF8F00).copy(alpha = 0.35f * intensity), 2.0f)

        // 3. Central 3D Sun Sphere with Radial Light Gradient
        val sunCenter3D = Vector3(0f, 0f, 0f).rotateX(rotPitch).rotateY(rotYaw)
        val (projCenter, scaleCenter) = sunCenter3D.project(400f, center)
        val rad = baseRadius * pulse * scaleCenter

        // Outer soft glow
        scope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFEE58).copy(alpha = 0.55f * intensity), Color.Transparent),
                center = projCenter,
                radius = rad * 2.2f
            ),
            radius = rad * 2.2f,
            center = projCenter
        )

        // Core Sphere
        scope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF9C4),
                    Color(0xFFFFCA28),
                    Color(0xFFFF8F00)
                ),
                center = projCenter - Offset(rad * 0.3f, rad * 0.3f),
                radius = rad
            ),
            radius = rad,
            center = projCenter
        )

        // Orbiting Solar Flare Sparkles
        for (j in 0..5) {
            val flareAngle = animTime * 0.8f + j * 1.05f
            val flareDist = baseRadius * (1.15f + sin(animTime * 3f + j) * 0.25f)
            val flareVec = Vector3(cos(flareAngle) * flareDist, sin(flareAngle) * flareDist, sin(flareAngle * 2f) * 25f)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
            val (flareProj, flareScale) = flareVec.project(400f, center)
            scope.drawCircle(
                color = Color.White.copy(alpha = 0.8f * intensity),
                radius = 3.5f * flareScale,
                center = flareProj
            )
        }
    }

    private fun drawMoonAndStars3D(
        scope: DrawScope,
        center: Offset,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float,
        moonScale: Float = 1.0f
    ) {
        // 1. Draw 3D Starfield with Parallax
        state.stars.forEach { star ->
            val rotated = star.pos.rotateX(rotPitch * 0.3f).rotateY(rotYaw * 0.3f + animTime * 0.02f)
            val (proj, scale) = rotated.project(400f, center)
            val twinkle = (sin(animTime * 2.5f + star.phase) * 0.4f + 0.6f).coerceIn(0.2f, 1f)
            val alpha = (twinkle * scale * 0.85f).coerceIn(0.1f, 1f)
            scope.drawCircle(
                color = Color(0xFFE2E8F0).copy(alpha = alpha),
                radius = star.size * scale,
                center = proj
            )
        }

        // 2. Shooting star
        if (state.shootingStarActive) {
            val p = state.shootingStarProgress
            val currentPos = state.shootingStarStart + (state.shootingStarEnd - state.shootingStarStart) * p
            val tailPos = state.shootingStarStart + (state.shootingStarEnd - state.shootingStarStart) * (p - 0.15f).coerceAtLeast(0f)

            val (projHead, scaleHead) = currentPos.project(400f, center)
            val (projTail, _) = tailPos.project(400f, center)

            scope.drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0xFF90CAF9), Color.White),
                    start = projTail,
                    end = projHead
                ),
                start = projTail,
                end = projHead,
                strokeWidth = 3f * scaleHead,
                cap = StrokeCap.Round
            )
        }

        // 3. Moon 3D Body
        val moonRadius = 50f * moonScale
        val moonVector = Vector3(0f, 0f, 0f).rotateX(rotPitch).rotateY(rotYaw)
        val (projMoon, scaleMoon) = moonVector.project(400f, center)
        val r = moonRadius * scaleMoon

        // Lunar glow
        scope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF81D4FA).copy(alpha = 0.35f), Color.Transparent),
                center = projMoon,
                radius = r * 2.2f
            ),
            radius = r * 2.2f,
            center = projMoon
        )

        // Moon disc
        scope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFECEFF1), Color(0xFFB0BEC5)),
                center = projMoon - Offset(r * 0.3f, r * 0.3f),
                radius = r
            ),
            radius = r,
            center = projMoon
        )

        // Craters in 3D
        val craters = listOf(
            Vector3(-14f, -12f, 15f) to 7f,
            Vector3(10f, -18f, 12f) to 9f,
            Vector3(16f, 12f, 14f) to 11f,
            Vector3(-8f, 16f, 16f) to 8f,
            Vector3(2f, 0f, 18f) to 6f
        )

        craters.forEach { (offsetVec, craterRad) ->
            val rotatedCrater = offsetVec.rotateX(rotPitch).rotateY(rotYaw)
            if (rotatedCrater.z > -10f) { // visible on front hemisphere
                val projCrater = projMoon + Offset(rotatedCrater.x * scaleMoon, rotatedCrater.y * scaleMoon)
                scope.drawCircle(
                    color = Color(0xFF90A4AE).copy(alpha = 0.45f),
                    radius = craterRad * scaleMoon,
                    center = projCrater
                )
            }
        }
    }

    private fun drawClouds3D(
        scope: DrawScope,
        center: Offset,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float,
        isDark: Boolean,
        hasSunlight: Boolean
    ) {
        // Sort puffs by projected Z for proper depth ordering
        val sortedPuffs = state.cloudPuffs.mapIndexed { index, puff ->
            // Add gentle floating motion to each puff
            val floatY = sin(animTime * 1.5f + index * 0.7f) * 4f
            val floatX = cos(animTime * 0.8f + index * 0.5f) * 3f
            val pos = Vector3(puff.center.x + floatX, puff.center.y + floatY, puff.center.z)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
            Triple(pos, puff.radius, puff.opacity)
        }.sortedBy { it.first.z } // back to front

        sortedPuffs.forEach { (pos, radius, opacity) ->
            val (proj, scale) = pos.project(400f, center)
            val rad = radius * scale

            val topColor = when {
                isDark -> Color(0xFF546E7A)
                hasSunlight -> Color(0xFFFFFFFF)
                else -> Color(0xFFECEFF1)
            }
            val bottomColor = when {
                isDark -> Color(0xFF263238)
                hasSunlight -> Color(0xFF90A4AE)
                else -> Color(0xFF78909C)
            }

            scope.drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(topColor.copy(alpha = opacity), bottomColor.copy(alpha = opacity)),
                    center = proj - Offset(rad * 0.25f, rad * 0.35f),
                    radius = rad
                ),
                radius = rad,
                center = proj
            )
        }
    }

    private fun drawRain3D(
        scope: DrawScope,
        center: Offset,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float,
        isHeavy: Boolean
    ) {
        // Ground ripple plane
        val groundY = 110f
        state.rainRipples.forEach { ripple ->
            val pos = Vector3(ripple.x, groundY, ripple.z)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
            val (proj, scale) = pos.project(400f, center)
            val rx = ripple.radius * scale
            val ry = ripple.radius * 0.4f * scale // foreshortened in perspective

            scope.drawOval(
                color = Color(0xFF81D4FA).copy(alpha = ripple.alpha.coerceIn(0f, 1f) * 0.6f),
                topLeft = Offset(proj.x - rx, proj.y - ry),
                size = Size(rx * 2f, ry * 2f),
                style = Stroke(width = 1.8f * scale)
            )
        }

        // Falling 3D rain streaks sorted by Z
        state.rainDrops.forEach { drop ->
            val startVec = Vector3(drop.x, drop.y, drop.z)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
            val endVec = Vector3(drop.x, drop.y + drop.length, drop.z)
                .rotateX(rotPitch)
                .rotateY(rotYaw)

            val (projStart, scale) = startVec.project(400f, center)
            val (projEnd, _) = endVec.project(400f, center)

            val alpha = (0.55f * scale).coerceIn(0.15f, 0.9f)
            val strokeW = if (isHeavy) 2.2f * scale else 1.4f * scale

            scope.drawLine(
                color = Color(0xFF64B5F6).copy(alpha = alpha),
                start = projStart,
                end = projEnd,
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }
    }

    private fun drawThunderstorm3D(
        scope: DrawScope,
        center: Offset,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float
    ) {
        // Dramatic ambient screen illumination on lightning flash
        if (state.lightningActive && state.lightningIntensity > 0.05f) {
            val flashColor = Color(0xFFEDE7F6).copy(alpha = (state.lightningIntensity * 0.45f).coerceIn(0f, 0.7f))
            scope.drawRect(color = flashColor)
        }

        // Dark stormy clouds
        drawClouds3D(scope, center + Offset(0f, -40f), state, rotPitch, rotYaw, animTime, isDark = true, hasSunlight = false)

        // 3D Branching Lightning Bolts
        if (state.lightningActive && state.lightningSegments.isNotEmpty()) {
            val boltAlpha = state.lightningIntensity.coerceIn(0f, 1f)
            state.lightningSegments.forEach { (p1, p2) ->
                val rotP1 = p1.rotateX(rotPitch).rotateY(rotYaw)
                val rotP2 = p2.rotateX(rotPitch).rotateY(rotYaw)

                val (proj1, s1) = rotP1.project(400f, center)
                val (proj2, _) = rotP2.project(400f, center)

                // Electric glow core & aura
                scope.drawLine(
                    color = Color(0xFFD1C4E9).copy(alpha = boltAlpha * 0.6f),
                    start = proj1,
                    end = proj2,
                    strokeWidth = 6f * s1,
                    cap = StrokeCap.Round
                )
                scope.drawLine(
                    color = Color.White.copy(alpha = boltAlpha),
                    start = proj1,
                    end = proj2,
                    strokeWidth = 2.5f * s1,
                    cap = StrokeCap.Round
                )
            }
        }

        // Heavy wind-driven rain
        drawRain3D(scope, center, state, rotPitch, rotYaw, animTime, isHeavy = true)
    }

    private fun drawSnow3D(
        scope: DrawScope,
        center: Offset,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float
    ) {
        // Snow accumulation horizon glow at bottom
        val groundProj = Vector3(0f, 120f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE1F5FE).copy(alpha = 0.4f), Color.Transparent),
                center = groundProj,
                radius = 160f
            ),
            topLeft = Offset(groundProj.x - 160f, groundProj.y - 25f),
            size = Size(320f, 50f)
        )

        // 3D Hexagonal and crystal snowflakes
        state.snowFlakes.forEach { flake ->
            val pos = Vector3(flake.x, flake.y, flake.z)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
            val (proj, scale) = pos.project(400f, center)
            val r = flake.radius * scale
            val alpha = (0.75f * scale).coerceIn(0.2f, 0.95f)

            // Draw crystalline snowflake arms
            val arms = 3 // 6 arms (3 lines through center)
            for (k in 0 until arms) {
                val a = (k.toFloat() / arms) * PI.toFloat() + flake.angleX
                val dx = cos(a) * r
                val dy = sin(a) * r
                scope.drawLine(
                    color = Color.White.copy(alpha = alpha),
                    start = proj - Offset(dx, dy),
                    end = proj + Offset(dx, dy),
                    strokeWidth = (1.5f * scale).coerceAtLeast(1f),
                    cap = StrokeCap.Round
                )
            }
            // Center crystalline pip
            scope.drawCircle(
                color = Color(0xFFE0F7FA).copy(alpha = alpha),
                radius = (1.2f * scale).coerceAtLeast(0.8f),
                center = proj
            )
        }
    }

    private fun drawFog3D(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float
    ) {
        // Multiple 3D rolling undulating fog planes
        val layers = 5
        for (i in 0 until layers) {
            val zLayer = -120f + i * 60f
            val yLayer = -20f + i * 25f
            val wavePhase = animTime * 0.8f + i * 1.3f

            val points = (0..24).map { step ->
                val x = -180f + step * 15f
                val y = yLayer + sin(x * 0.03f + wavePhase) * 16f
                Vector3(x, y, zLayer).rotateX(rotPitch).rotateY(rotYaw)
            }

            val path = Path()
            var first = true
            points.forEach { pt ->
                val (proj, _) = pt.project(400f, center)
                if (first) {
                    path.moveTo(proj.x, proj.y)
                    first = false
                } else {
                    path.lineTo(proj.x, proj.y)
                }
            }

            scope.drawPath(
                path = path,
                color = Color(0xFFCFD8DC).copy(alpha = 0.35f),
                style = Stroke(width = 24f, cap = StrokeCap.Round)
            )
        }
    }

    private fun drawWind3D(
        scope: DrawScope,
        center: Offset,
        state: Weather3DSceneState,
        rotPitch: Float,
        rotYaw: Float,
        animTime: Float
    ) {
        // Spiraling 3D wind streamline vectors
        state.windStreamlines.forEach { line ->
            val angleStart = animTime * line.speed + line.phase
            val angleEnd = angleStart + line.lengthAngle

            val steps = 16
            val points = (0..steps).map { s ->
                val frac = s.toFloat() / steps
                val a = angleStart + frac * line.lengthAngle
                val r = line.radius + sin(frac * PI.toFloat()) * 10f
                val h = line.height + frac * 20f
                Vector3(cos(a) * r, h, sin(a) * r)
                    .rotateX(rotPitch)
                    .rotateY(rotYaw)
            }

            for (idx in 0 until points.size - 1) {
                val (p1, s1) = points[idx].project(400f, center)
                val (p2, _) = points[idx + 1].project(400f, center)
                val alpha = ((idx.toFloat() / steps) * 0.7f * s1).coerceIn(0.1f, 0.8f)

                scope.drawLine(
                    color = Color(0xFF80CBC4).copy(alpha = alpha),
                    start = p1,
                    end = p2,
                    strokeWidth = 2.5f * s1,
                    cap = StrokeCap.Round
                )
            }
        }

        // Swirling debris/leaves in 3D
        for (leafIdx in 0..7) {
            val a = animTime * 1.8f + leafIdx * 0.8f
            val r = 85f + (leafIdx % 3) * 20f
            val leafPos = Vector3(cos(a) * r, -40f + sin(a * 2f) * 35f, sin(a) * r)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
            val (proj, scale) = leafPos.project(400f, center)

            scope.drawCircle(
                color = Color(0xFFA5D6A7).copy(alpha = 0.8f * scale),
                radius = 3.5f * scale,
                center = proj
            )
        }
    }

    private fun draw3DLoop(
        scope: DrawScope,
        center: Offset,
        points: List<Vector3>,
        color: Color,
        strokeWidth: Float
    ) {
        for (i in points.indices) {
            val nextIdx = (i + 1) % points.size
            val (p1, s1) = points[i].project(400f, center)
            val (p2, _) = points[nextIdx].project(400f, center)
            scope.drawLine(
                color = color,
                start = p1,
                end = p2,
                strokeWidth = strokeWidth * s1,
                cap = StrokeCap.Round
            )
        }
    }
}
