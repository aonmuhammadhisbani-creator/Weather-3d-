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

object Landmark3DModels {

    fun renderLandmark(
        drawScope: DrawScope,
        landmark: FamousLandmark,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        condition: WeatherCondition,
        animTime: Float,
        lightningIntensity: Float = 0f
    ) {
        val isDay = condition.isDaytime
        val isSnowing = condition == WeatherCondition.SNOW
        val isRaining = condition == WeatherCondition.LIGHT_RAIN || condition == WeatherCondition.HEAVY_RAIN || condition == WeatherCondition.THUNDERSTORM

        // Base ground plateau with subtle perspective ring
        drawGroundPlane(drawScope, center, rotPitch, rotYaw, isDay, isSnowing, isRaining, lightningIntensity)

        when (landmark) {
            FamousLandmark.MOUNT_FUJI_PAGODA -> {
                drawMountFujiAndPagoda(drawScope, center, rotPitch, rotYaw, isDay, isSnowing, animTime, lightningIntensity)
            }
            FamousLandmark.STATUE_OF_LIBERTY_SKYLINE -> {
                drawStatueOfLibertySkyline(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
            FamousLandmark.EIFFEL_TOWER -> {
                drawEiffelTower(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
            FamousLandmark.BIG_BEN_WESTMINSTER -> {
                drawBigBen(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
            FamousLandmark.BURJ_KHALIFA -> {
                drawBurjKhalifa(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
            FamousLandmark.SYDNEY_OPERA_HOUSE -> {
                drawSydneyOpera(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
            FamousLandmark.COLOSSEUM -> {
                drawColosseum(drawScope, center, rotPitch, rotYaw, isDay, isSnowing, lightningIntensity)
            }
            FamousLandmark.TAJ_MAHAL -> {
                drawTajMahal(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
            FamousLandmark.PYRAMIDS_OF_GIZA -> {
                drawPyramids(drawScope, center, rotPitch, rotYaw, isDay, lightningIntensity)
            }
            FamousLandmark.CHRIST_THE_REDEEMER -> {
                drawChristRedeemer(drawScope, center, rotPitch, rotYaw, isDay, lightningIntensity)
            }
            FamousLandmark.GREAT_WALL_PAGODA -> {
                drawGreatWall(drawScope, center, rotPitch, rotYaw, isDay, isSnowing, lightningIntensity)
            }
            FamousLandmark.BRANDENBURG_GATE -> {
                drawBrandenburgGate(drawScope, center, rotPitch, rotYaw, isDay, lightningIntensity)
            }
            FamousLandmark.GLOBAL_ICONIC_CITY -> {
                drawGlobalCity(drawScope, center, rotPitch, rotYaw, isDay, animTime, lightningIntensity)
            }
        }
    }

    private fun drawGroundPlane(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        isSnowing: Boolean,
        isRaining: Boolean,
        lightningFlash: Float
    ) {
        val groundY = 115f
        val groundPoints = (0..32).map { i ->
            val a = (i.toFloat() / 32f) * 2f * PI.toFloat()
            val rx = 160f
            val rz = 110f
            Vector3(cos(a) * rx, groundY, sin(a) * rz)
                .rotateX(rotPitch)
                .rotateY(rotYaw)
        }

        val groundPath = Path()
        groundPoints.forEachIndexed { idx, pt ->
            val (proj, _) = pt.project(400f, center)
            if (idx == 0) groundPath.moveTo(proj.x, proj.y) else groundPath.lineTo(proj.x, proj.y)
        }
        groundPath.close()

        val groundColor = when {
            lightningFlash > 0.1f -> Color(0xFF6B5B95).copy(alpha = 0.5f)
            isSnowing -> Color(0xFFE2E8F0).copy(alpha = 0.55f)
            isRaining -> Color(0xFF1E293B).copy(alpha = 0.65f)
            isDay -> Color(0xFF334155).copy(alpha = 0.45f)
            else -> Color(0xFF0F172A).copy(alpha = 0.55f)
        }

        scope.drawPath(groundPath, color = groundColor, style = Fill)
        scope.drawPath(
            groundPath,
            color = if (isDay) Color.White.copy(alpha = 0.25f) else Color(0xFF64B5F6).copy(alpha = 0.2f),
            style = Stroke(width = 1.5f)
        )
    }

    // 1. JAPAN: Mount Fuji + 5-Story Chureito Pagoda & Torii Gate
    private fun drawMountFujiAndPagoda(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        isSnowing: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val fujiBaseY = 115f
        val fujiPeakY = -20f

        // Mount Fuji 3D volcanic cone facets
        val peak = Vector3(15f, fujiPeakY, -40f).rotateX(rotPitch).rotateY(rotYaw)
        val leftFoot = Vector3(-130f, fujiBaseY, -50f).rotateX(rotPitch).rotateY(rotYaw)
        val rightFoot = Vector3(160f, fujiBaseY, -45f).rotateX(rotPitch).rotateY(rotYaw)
        val frontFoot = Vector3(20f, fujiBaseY, -10f).rotateX(rotPitch).rotateY(rotYaw)

        val (projPeak, _) = peak.project(400f, center)
        val (projLeft, _) = leftFoot.project(400f, center)
        val (projRight, _) = rightFoot.project(400f, center)
        val (projFront, _) = frontFoot.project(400f, center)

        // Left facet (shadowed)
        val leftFacet = Path().apply {
            moveTo(projPeak.x, projPeak.y)
            lineTo(projLeft.x, projLeft.y)
            lineTo(projFront.x, projFront.y)
            close()
        }
        val leftCol = if (isDay) Color(0xFF334155).copy(alpha = 0.85f) else Color(0xFF1E293B).copy(alpha = 0.9f)
        scope.drawPath(leftFacet, color = leftCol)

        // Right facet (lit)
        val rightFacet = Path().apply {
            moveTo(projPeak.x, projPeak.y)
            lineTo(projFront.x, projFront.y)
            lineTo(projRight.x, projRight.y)
            close()
        }
        val rightCol = if (isDay) Color(0xFF475569).copy(alpha = 0.85f) else Color(0xFF0F172A).copy(alpha = 0.9f)
        scope.drawPath(rightFacet, color = rightCol)

        // Iconic Snow Cap on Mount Fuji summit
        val snowLeft = Vector3(-45f, 35f, -42f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val snowFront = Vector3(18f, 38f, -22f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val snowRight = Vector3(75f, 35f, -38f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        val snowCapPath = Path().apply {
            moveTo(projPeak.x, projPeak.y)
            lineTo(snowLeft.x, snowLeft.y)
            lineTo(snowFront.x, snowFront.y)
            lineTo(snowRight.x, snowRight.y)
            close()
        }
        scope.drawPath(snowCapPath, color = Color.White.copy(alpha = 0.92f))

        // Traditional 5-Story Pagoda in foreground
        val pagodaBaseX = -65f
        val pagodaBaseZ = 45f
        val numTiers = 5
        for (tier in 0 until numTiers) {
            val tierY = 115f - (tier * 16f)
            val tierWidth = 32f - (tier * 3.5f)
            val tierRoofY = tierY - 10f

            val pLeft = Vector3(pagodaBaseX - tierWidth / 2f, tierRoofY, pagodaBaseZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val pRight = Vector3(pagodaBaseX + tierWidth / 2f, tierRoofY, pagodaBaseZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val pTop = Vector3(pagodaBaseX, tierRoofY - 5f, pagodaBaseZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

            // Curved roof eave
            val roofPath = Path().apply {
                moveTo(pLeft.x, pLeft.y)
                lineTo(pTop.x, pTop.y)
                lineTo(pRight.x, pRight.y)
                close()
            }
            scope.drawPath(roofPath, color = Color(0xFFDC2626)) // Vermilion red

            // Lantern glow in night mode
            if (!isDay) {
                val lanternPos = Vector3(pagodaBaseX, tierY - 4f, pagodaBaseZ + 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
                scope.drawCircle(color = Color(0xFFFBBF24).copy(alpha = 0.8f), radius = 2.5f, center = lanternPos)
            }
        }

        // Pagoda Spire (Sorin)
        val spireBase = Vector3(pagodaBaseX, 115f - (numTiers * 16f) - 6f, pagodaBaseZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val spireTip = Vector3(pagodaBaseX, 115f - (numTiers * 16f) - 24f, pagodaBaseZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = Color(0xFFFBBF24), start = spireBase, end = spireTip, strokeWidth = 2.5f, cap = StrokeCap.Round)

        // Red Torii Gate on the right foreground
        val toriiX = 75f
        val toriiZ = 45f
        val tLBase = Vector3(toriiX - 14f, 115f, toriiZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val tLTop = Vector3(toriiX - 14f, 85f, toriiZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val tRBase = Vector3(toriiX + 14f, 115f, toriiZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val tRTop = Vector3(toriiX + 14f, 85f, toriiZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val tBeamL = Vector3(toriiX - 22f, 82f, toriiZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val tBeamR = Vector3(toriiX + 22f, 82f, toriiZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        scope.drawLine(color = Color(0xFFDC2626), start = tLBase, end = tLTop, strokeWidth = 3f)
        scope.drawLine(color = Color(0xFFDC2626), start = tRBase, end = tRTop, strokeWidth = 3f)
        scope.drawLine(color = Color(0xFFDC2626), start = tBeamL, end = tBeamR, strokeWidth = 4f, cap = StrokeCap.Round)
    }

    // 2. UNITED STATES: Statue of Liberty & Manhattan 3D Skyline
    private fun drawStatueOfLibertySkyline(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f

        // Manhattan Skyline in background
        val buildings = listOf(
            Triple(-90f, 65f, 28f),
            Triple(-60f, 40f, 24f),
            Triple(-35f, 55f, 22f),
            Triple(-10f, 30f, 32f),  // Empire state tower
            Triple(25f, 50f, 26f),
            Triple(55f, 65f, 22f)
        )

        buildings.forEach { (bx, bHeight, bWidth) ->
            val bTop = Vector3(bx, groundY - bHeight, -40f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val bBottom = Vector3(bx, groundY, -40f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val bColor = if (isDay) Color(0xFF475569) else Color(0xFF1E293B)

            scope.drawLine(
                color = bColor,
                start = bTop,
                end = bBottom,
                strokeWidth = bWidth,
                cap = StrokeCap.Square
            )

            // Night window lit dots
            if (!isDay) {
                for (row in 1..4) {
                    val winY = groundY - bHeight + row * 8f
                    val wPt = Vector3(bx, winY, -38f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
                    scope.drawCircle(color = Color(0xFFFDE047).copy(alpha = 0.7f), radius = 1.5f, center = wPt)
                }
            }
        }

        // Statue of Liberty in foreground right
        val libX = 40f
        val libZ = 35f
        val pedTopY = 85f
        val pedBaseY = groundY

        // Pedestal
        val pedB = Vector3(libX, pedBaseY, libZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val pedT = Vector3(libX, pedTopY, libZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = Color(0xFF94A3B8), start = pedB, end = pedT, strokeWidth = 24f)

        // Statue Body & Crown (Verdigris patina green)
        val statueCol = Color(0xFF5EEAD4)
        val bodyT = Vector3(libX, 50f, libZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val headT = Vector3(libX, 42f, libZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = statueCol, start = pedT, end = bodyT, strokeWidth = 14f)
        scope.drawCircle(color = statueCol, radius = 5f, center = headT)

        // Raised Right Arm holding the Torch
        val torchBase = Vector3(libX + 8f, 48f, libZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val torchFlame = Vector3(libX + 16f, 26f, libZ).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = statueCol, start = torchBase, end = torchFlame, strokeWidth = 3.5f)

        // Glowing Torch Flame
        scope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF08A), Color(0xFFEAB308), Color.Transparent),
                center = torchFlame,
                radius = 12f
            ),
            radius = 12f,
            center = torchFlame
        )
    }

    // 3. FRANCE: Eiffel Tower with Arches, Platforms, and Glowing Beacon
    private fun drawEiffelTower(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f
        val towerCol = if (isDay) Color(0xFF78716C) else Color(0xFFA8A29E)

        // 4 Legs
        val legFL = Vector3(-35f, groundY, 20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val legFR = Vector3(35f, groundY, 20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val legBL = Vector3(-25f, groundY, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val legBR = Vector3(25f, groundY, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        // First Platform
        val plat1L = Vector3(-22f, 75f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val plat1R = Vector3(22f, 75f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        scope.drawLine(color = towerCol, start = legFL, end = plat1L, strokeWidth = 5f)
        scope.drawLine(color = towerCol, start = legFR, end = plat1R, strokeWidth = 5f)
        scope.drawLine(color = towerCol.copy(alpha = 0.6f), start = legBL, end = plat1L, strokeWidth = 4f)
        scope.drawLine(color = towerCol.copy(alpha = 0.6f), start = legBR, end = plat1R, strokeWidth = 4f)

        // Platform 1 deck
        scope.drawLine(color = towerCol, start = plat1L, end = plat1R, strokeWidth = 6f)

        // Base Arch
        val archPeak = Vector3(0f, 90f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val archPath = Path().apply {
            moveTo(legFL.x, legFL.y)
            quadraticTo(archPeak.x, archPeak.y, legFR.x, legFR.y)
        }
        scope.drawPath(archPath, color = towerCol, style = Stroke(width = 3.5f))

        // Second Platform
        val plat2L = Vector3(-12f, 45f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val plat2R = Vector3(12f, 45f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = towerCol, start = plat1L, end = plat2L, strokeWidth = 4f)
        scope.drawLine(color = towerCol, start = plat1R, end = plat2R, strokeWidth = 4f)
        scope.drawLine(color = towerCol, start = plat2L, end = plat2R, strokeWidth = 5f)

        // Spire to Lantern Top
        val spireTip = Vector3(0f, -15f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = towerCol, start = plat2L, end = spireTip, strokeWidth = 3f)
        scope.drawLine(color = towerCol, start = plat2R, end = spireTip, strokeWidth = 3f)

        // Searchlight beacon rotating at top
        val beaconAngle = animTime * 1.5f
        val beamEnd = Vector3(cos(beaconAngle) * 90f, -25f, sin(beaconAngle) * 90f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(
            brush = Brush.linearGradient(listOf(Color(0xFFFEF08A), Color.Transparent), start = spireTip, end = beamEnd),
            start = spireTip,
            end = beamEnd,
            strokeWidth = 14f,
            cap = StrokeCap.Round
        )
        scope.drawCircle(color = Color(0xFFFEF08A), radius = 4f, center = spireTip)
    }

    // 4. UNITED KINGDOM: Big Ben Clock Tower & Palace of Westminster
    private fun drawBigBen(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f
        val stoneCol = if (isDay) Color(0xFFD97706) else Color(0xFF92400E)

        // Parliament lower arcade on the left
        val arcL = Vector3(-95f, groundY, -10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val arcR = Vector3(-15f, groundY, -10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val arcT = Vector3(-15f, 75f, -10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawRect(
            color = stoneCol.copy(alpha = 0.7f),
            topLeft = Offset(arcL.x, arcT.y),
            size = Size(arcR.x - arcL.x, groundY - arcT.y)
        )

        // Big Ben Tower Shaft
        val towerX = 15f
        val towerWidth = 32f
        val towerBase = Vector3(towerX, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val belfryY = 25f
        val towerBelfry = Vector3(towerX, belfryY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = stoneCol, start = towerBase, end = towerBelfry, strokeWidth = towerWidth)

        // Clock Face Dial (illuminated golden circle)
        val clockPos = Vector3(towerX, 35f, 8f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawCircle(color = Color(0xFFFEF08A), radius = 7f, center = clockPos)
        scope.drawCircle(color = Color.Black, radius = 7f, center = clockPos, style = Stroke(width = 1.5f))

        // Clock Hands
        scope.drawLine(color = Color.Black, start = clockPos, end = clockPos - Offset(0f, 4f), strokeWidth = 1.5f)
        scope.drawLine(color = Color.Black, start = clockPos, end = clockPos + Offset(3f, 0f), strokeWidth = 1.5f)

        // Tower Roof Spire
        val spireTip = Vector3(towerX, -25f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val spireLeft = Vector3(towerX - 16f, belfryY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val spireRight = Vector3(towerX + 16f, belfryY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        val roofPath = Path().apply {
            moveTo(spireLeft.x, spireLeft.y)
            lineTo(spireTip.x, spireTip.y)
            lineTo(spireRight.x, spireRight.y)
            close()
        }
        scope.drawPath(roofPath, color = Color(0xFF1E293B))
    }

    // 5. UAE: Burj Khalifa Soaring Stepped Spire
    private fun drawBurjKhalifa(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f
        val tiers = listOf(
            Triple(115f, 85f, 38f),
            Triple(85f, 55f, 30f),
            Triple(55f, 25f, 22f),
            Triple(25f, -5f, 15f),
            Triple(-5f, -35f, 9f),
            Triple(-35f, -60f, 4f)
        )

        tiers.forEachIndexed { idx, (bY, tY, width) ->
            val bPt = Vector3(0f, bY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val tPt = Vector3(0f, tY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

            val col = if (isDay) Color(0xFF64748B) else Color(0xFF38BDF8).copy(alpha = 0.85f)
            scope.drawLine(color = col, start = bPt, end = tPt, strokeWidth = width)

            // Futuristic LED Neon edge stripes
            if (!isDay) {
                val ledX = (sin(animTime * 3f + idx) * width * 0.4f).toFloat()
                scope.drawLine(
                    color = Color.White,
                    start = Offset(bPt.x + ledX, bPt.y),
                    end = Offset(tPt.x + ledX, tPt.y),
                    strokeWidth = 1.5f
                )
            }
        }

        // Top Spire Needle
        val spireB = Vector3(0f, -60f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val spireT = Vector3(0f, -80f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = Color.White, start = spireB, end = spireT, strokeWidth = 2f)
        scope.drawCircle(color = Color(0xFFEF4444), radius = 3f, center = spireT) // Aircraft warning beacon
    }

    // 6. AUSTRALIA: Sydney Opera House Sail Shells & Harbour Bridge
    private fun drawSydneyOpera(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f

        // Harbour Bridge Arch in background
        val bL = Vector3(-110f, groundY, -60f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val bR = Vector3(-30f, groundY, -60f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val bApex = Vector3(-70f, 35f, -60f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val archPath = Path().apply {
            moveTo(bL.x, bL.y)
            quadraticTo(bApex.x, bApex.y, bR.x, bR.y)
        }
        scope.drawPath(archPath, color = Color(0xFF64748B), style = Stroke(width = 4.5f))

        // Granite Podium Platform
        val podL = Vector3(-30f, groundY, 10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val podR = Vector3(90f, groundY, 10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = Color(0xFF475569), start = podL, end = podR, strokeWidth = 14f)

        // 3 Distinct Sail Shells
        val shells = listOf(
            Triple(0f, 65f, 32f),
            Triple(28f, 50f, 26f),
            Triple(54f, 60f, 22f)
        )

        shells.forEach { (sx, peakHeight, width) ->
            val sLeft = Vector3(sx - width / 2f, groundY - 7f, 15f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val sRight = Vector3(sx + width / 2f, groundY - 7f, 15f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val sPeak = Vector3(sx - 4f, groundY - peakHeight, 15f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

            val shellPath = Path().apply {
                moveTo(sLeft.x, sLeft.y)
                quadraticTo(sPeak.x - 10f, sPeak.y, sPeak.x, sPeak.y)
                quadraticTo(sPeak.x + 8f, sPeak.y + 15f, sRight.x, sRight.y)
                close()
            }

            scope.drawPath(shellPath, color = Color(0xFFF8FAFC)) // White ceramic shells
            scope.drawPath(shellPath, color = Color(0xFFCBD5E1), style = Stroke(width = 1.5f))
        }
    }

    // 7. ITALY: The Roman Colosseum
    private fun drawColosseum(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        isSnow: Boolean,
        flash: Float
    ) {
        val groundY = 115f
        val stoneCol = if (isDay) Color(0xFFD97706) else Color(0xFF78350F)

        val colWidth = 90f
        val colHeight = 55f

        val cL = Vector3(-colWidth, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val cR = Vector3(colWidth, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val cT = Vector3(0f, groundY - colHeight, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        // Outer Tier Wall
        scope.drawRect(
            color = stoneCol,
            topLeft = Offset(cL.x, cT.y),
            size = Size(cR.x - cL.x, groundY - cT.y)
        )

        // Arched Portals along 3 tiers
        for (tier in 1..3) {
            val tierY = groundY - (tier * 15f)
            for (col in -3..3) {
                val archX = col * 22f
                val archPos = Vector3(archX, tierY, 15f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
                scope.drawCircle(
                    color = Color.Black.copy(alpha = 0.55f),
                    radius = 4f,
                    center = archPos
                )
            }
        }
    }

    // 8. INDIA: Taj Mahal Dome, Minarets, and Symmetrical Pool
    private fun drawTajMahal(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f
        val marbleCol = Color(0xFFF1F5F9)

        // 4 Corner Minarets
        val minarets = listOf(-75f, -50f, 50f, 75f)
        minarets.forEach { mx ->
            val mBase = Vector3(mx, groundY, -10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val mTop = Vector3(mx, 35f, -10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            scope.drawLine(color = marbleCol, start = mBase, end = mTop, strokeWidth = 5f)
            scope.drawCircle(color = marbleCol, radius = 4f, center = mTop)
        }

        // Central Mausoleum Block
        val baseL = Vector3(-40f, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val baseR = Vector3(40f, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val baseT = Vector3(0f, 55f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawRect(
            color = marbleCol,
            topLeft = Offset(baseL.x, baseT.y),
            size = Size(baseR.x - baseL.x, groundY - baseT.y)
        )

        // Grand Central Arched Iwan
        val iwanPos = Vector3(0f, 85f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawCircle(color = Color(0xFF334155), radius = 12f, center = iwanPos)

        // Central Onion Dome
        val domeCenter = Vector3(0f, 40f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawCircle(color = marbleCol, radius = 22f, center = domeCenter)

        // Gold Finial Spire
        val finialTop = Vector3(0f, 10f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = Color(0xFFFBBF24), start = domeCenter, end = finialTop, strokeWidth = 2.5f)
    }

    // 9. EGYPT: Great Pyramids of Giza
    private fun drawPyramids(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        flash: Float
    ) {
        val groundY = 115f
        val sandLit = if (isDay) Color(0xFFF59E0B) else Color(0xFF78350F)
        val sandShadow = if (isDay) Color(0xFFB45309) else Color(0xFF451A03)

        // Khufu Great Pyramid (Left/Center)
        val p1Apex = Vector3(-25f, 25f, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val p1L = Vector3(-110f, groundY, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val p1R = Vector3(45f, groundY, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val p1Front = Vector3(-20f, groundY, 25f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        val p1FaceLit = Path().apply {
            moveTo(p1Apex.x, p1Apex.y)
            lineTo(p1Front.x, p1Front.y)
            lineTo(p1R.x, p1R.y)
            close()
        }
        val p1FaceShadow = Path().apply {
            moveTo(p1Apex.x, p1Apex.y)
            lineTo(p1L.x, p1L.y)
            lineTo(p1Front.x, p1Front.y)
            close()
        }
        scope.drawPath(p1FaceLit, color = sandLit)
        scope.drawPath(p1FaceShadow, color = sandShadow)

        // Khafre Pyramid (Right/Smaller)
        val p2Apex = Vector3(65f, 45f, -50f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val p2L = Vector3(15f, groundY, -50f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val p2R = Vector3(120f, groundY, -50f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val p2Front = Vector3(70f, groundY, -10f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first

        val p2FaceLit = Path().apply {
            moveTo(p2Apex.x, p2Apex.y)
            lineTo(p2Front.x, p2Front.y)
            lineTo(p2R.x, p2R.y)
            close()
        }
        val p2FaceShadow = Path().apply {
            moveTo(p2Apex.x, p2Apex.y)
            lineTo(p2L.x, p2L.y)
            lineTo(p2Front.x, p2Front.y)
            close()
        }
        scope.drawPath(p2FaceLit, color = sandLit.copy(alpha = 0.9f))
        scope.drawPath(p2FaceShadow, color = sandShadow.copy(alpha = 0.9f))
    }

    // 10. BRAZIL: Christ the Redeemer on Mount Corcovado
    private fun drawChristRedeemer(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        flash: Float
    ) {
        val groundY = 115f

        // Mount Corcovado Peak
        val mPeak = Vector3(0f, 65f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val mL = Vector3(-80f, groundY, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val mR = Vector3(80f, groundY, -20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val mPath = Path().apply {
            moveTo(mL.x, mL.y)
            lineTo(mPeak.x, mPeak.y)
            lineTo(mR.x, mR.y)
            close()
        }
        scope.drawPath(mPath, color = Color(0xFF1E3A1E)) // Lush tropical granite peak

        // Pedestal
        val pedB = Vector3(0f, 65f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val pedT = Vector3(0f, 50f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = Color(0xFF94A3B8), start = pedB, end = pedT, strokeWidth = 14f)

        // Statue Body & Head
        val statCol = Color(0xFFE2E8F0)
        val bodyT = Vector3(0f, 20f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val head = Vector3(0f, 12f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = statCol, start = pedT, end = bodyT, strokeWidth = 8f)
        scope.drawCircle(color = statCol, radius = 4f, center = head)

        // Outstretched Open Arms (embracing the skyline)
        val armL = Vector3(-35f, 22f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val armR = Vector3(35f, 22f, 5f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawLine(color = statCol, start = armL, end = armR, strokeWidth = 4f, cap = StrokeCap.Round)
    }

    // 11. CHINA: Great Wall & Imperial Chinese Pagoda
    private fun drawGreatWall(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        isSnow: Boolean,
        flash: Float
    ) {
        val groundY = 115f
        val wallCol = Color(0xFF64748B)

        // Winding fortress wall segments across mountain ridges
        val wallPts = listOf(
            Vector3(-120f, 105f, -30f),
            Vector3(-75f, 75f, -10f),
            Vector3(-20f, 95f, 10f),
            Vector3(40f, 65f, 20f),
            Vector3(100f, 85f, 40f)
        ).map { it.rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first }

        for (i in 0 until wallPts.size - 1) {
            scope.drawLine(color = wallCol, start = wallPts[i], end = wallPts[i + 1], strokeWidth = 12f)
        }

        // Watchtower Pavilion at mid-ridge
        val towerBase = Vector3(40f, 65f, 20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val towerRoof = Vector3(40f, 40f, 20f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawRect(
            color = Color(0xFFDC2626),
            topLeft = Offset(towerRoof.x - 14f, towerRoof.y),
            size = Size(28f, towerBase.y - towerRoof.y)
        )
        // Flared Imperial Chinese Roof
        val roofPath = Path().apply {
            moveTo(towerRoof.x - 22f, towerRoof.y + 4f)
            lineTo(towerRoof.x, towerRoof.y - 8f)
            lineTo(towerRoof.x + 22f, towerRoof.y + 4f)
            close()
        }
        scope.drawPath(roofPath, color = Color(0xFFF59E0B)) // Imperial golden yellow roof
    }

    // 12. GERMANY: Brandenburg Gate
    private fun drawBrandenburgGate(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        flash: Float
    ) {
        val groundY = 115f
        val stoneCol = if (isDay) Color(0xFFE2E8F0) else Color(0xFF64748B)

        // 6 Neoclassical Doric Columns
        for (i in -2..3) {
            val colX = (i * 18f) - 9f
            val cBase = Vector3(colX, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val cTop = Vector3(colX, 55f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            scope.drawLine(color = stoneCol, start = cBase, end = cTop, strokeWidth = 4.5f)
        }

        // Attic Frieze Block
        val fL = Vector3(-55f, 55f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val fR = Vector3(55f, 55f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val fT = Vector3(0f, 40f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawRect(
            color = stoneCol,
            topLeft = Offset(fL.x, fT.y),
            size = Size(fR.x - fL.x, 15f)
        )

        // Quadriga Chariot on top
        val quadPos = Vector3(0f, 30f, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        scope.drawCircle(color = Color(0xFF10B981), radius = 6f, center = quadPos) // Bronze patina
    }

    // 13. GLOBAL / WORLDWIDE FALLBACK: Iconic Metropolis & Mountain Peaks
    private fun drawGlobalCity(
        scope: DrawScope,
        center: Offset,
        rotPitch: Float,
        rotYaw: Float,
        isDay: Boolean,
        animTime: Float,
        flash: Float
    ) {
        val groundY = 115f

        // Mountain ridge in background
        val ridge = listOf(
            Vector3(-140f, groundY, -50f),
            Vector3(-70f, 40f, -50f),
            Vector3(10f, 65f, -50f),
            Vector3(70f, 35f, -50f),
            Vector3(140f, groundY, -50f)
        ).map { it.rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first }

        val ridgePath = Path().apply {
            moveTo(ridge[0].x, ridge[0].y)
            for (p in ridge) lineTo(p.x, p.y)
            close()
        }
        scope.drawPath(ridgePath, color = if (isDay) Color(0xFF334155).copy(alpha = 0.7f) else Color(0xFF1E293B))

        // Modern 3D Tower cluster
        val towers = listOf(
            Triple(-50f, 65f, 24f),
            Triple(-15f, 85f, 20f), // Center Spire
            Triple(20f, 55f, 22f),
            Triple(50f, 70f, 18f)
        )

        towers.forEach { (tx, height, width) ->
            val tB = Vector3(tx, groundY, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val tT = Vector3(tx, groundY - height, 0f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
            val col = if (isDay) Color(0xFF64748B) else Color(0xFF38BDF8).copy(alpha = 0.85f)
            scope.drawLine(color = col, start = tB, end = tT, strokeWidth = width)

            if (!isDay) {
                // glowing windows
                scope.drawCircle(color = Color(0xFFFEF08A), radius = 2f, center = tT + Offset(0f, 12f))
            }
        }

        // Suspension Bridge in foreground
        val pL = Vector3(-80f, 85f, 30f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val pR = Vector3(80f, 85f, 30f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val mid = Vector3(0f, 105f, 30f).rotateX(rotPitch).rotateY(rotYaw).project(400f, center).first
        val cablePath = Path().apply {
            moveTo(pL.x, pL.y)
            quadraticTo(mid.x, mid.y, pR.x, pR.y)
        }
        scope.drawPath(cablePath, color = Color(0xFFEF4444), style = Stroke(width = 2.5f))
    }
}
