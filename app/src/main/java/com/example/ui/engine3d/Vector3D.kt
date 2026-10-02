package com.example.ui.engine3d

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

data class Vector3(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun plus(other: Vector3): Vector3 = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3): Vector3 = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(factor: Float): Vector3 = Vector3(x * factor, y * factor, z * factor)

    fun rotateX(angleRad: Float): Vector3 {
        val c = cos(angleRad)
        val s = sin(angleRad)
        val ny = y * c - z * s
        val nz = y * s + z * c
        return Vector3(x, ny, nz)
    }

    fun rotateY(angleRad: Float): Vector3 {
        val c = cos(angleRad)
        val s = sin(angleRad)
        val nx = x * c + z * s
        val nz = -x * s + z * c
        return Vector3(nx, y, nz)
    }

    fun rotateZ(angleRad: Float): Vector3 {
        val c = cos(angleRad)
        val s = sin(angleRad)
        val nx = x * c - y * s
        val ny = x * s + y * c
        return Vector3(nx, ny, z)
    }

    /**
     * Projects 3D vector to 2D screen coordinate.
     * @param cameraZ distance from camera to origin
     * @param center screen center offset
     * @return Pair(ScreenOffset, scaleFactor)
     */
    fun project(cameraZ: Float = 420f, center: Offset): Pair<Offset, Float> {
        val denom = (cameraZ + z).coerceAtLeast(40f)
        val scale = (cameraZ / denom).coerceIn(0.15f, 3.5f)
        val sx = center.x + x * scale
        val sy = center.y + y * scale
        return Pair(Offset(sx, sy), scale)
    }
}
