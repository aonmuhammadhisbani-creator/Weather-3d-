package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.WeatherCondition
import com.example.ui.engine3d.Vector3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SkySphere 3D", appName)
    }

    @Test
    fun `weather condition wmo code mapping`() {
        val sunny = WeatherCondition.fromWmoCode(0, 1)
        assertEquals(WeatherCondition.CLEAR_DAY, sunny)

        val night = WeatherCondition.fromWmoCode(0, 0)
        assertEquals(WeatherCondition.CLEAR_NIGHT, night)

        val rain = WeatherCondition.fromWmoCode(63, 1)
        assertEquals(WeatherCondition.HEAVY_RAIN, rain)

        val thunder = WeatherCondition.fromWmoCode(95, 0)
        assertEquals(WeatherCondition.THUNDERSTORM, thunder)
    }

    @Test
    fun `vector3 rotation math produces valid values`() {
        val v = Vector3(10f, 0f, 0f)
        val rotated = v.rotateZ(Math.PI.toFloat() / 2f)
        assertEquals(0f, rotated.x, 0.001f)
        assertEquals(10f, rotated.y, 0.001f)
    }

    @Test
    fun `famous landmark location mapping works for various countries`() {
        val japan = com.example.ui.engine3d.FamousLandmark.forLocation("Japan", "Tokyo")
        assertEquals(com.example.ui.engine3d.FamousLandmark.MOUNT_FUJI_PAGODA, japan)

        val france = com.example.ui.engine3d.FamousLandmark.forLocation("France", "Paris")
        assertEquals(com.example.ui.engine3d.FamousLandmark.EIFFEL_TOWER, france)

        val usa = com.example.ui.engine3d.FamousLandmark.forLocation("United States", "New York")
        assertEquals(com.example.ui.engine3d.FamousLandmark.STATUE_OF_LIBERTY_SKYLINE, usa)

        val uae = com.example.ui.engine3d.FamousLandmark.forLocation("United Arab Emirates", "Dubai")
        assertEquals(com.example.ui.engine3d.FamousLandmark.BURJ_KHALIFA, uae)

        val uk = com.example.ui.engine3d.FamousLandmark.forLocation("United Kingdom", "London")
        assertEquals(com.example.ui.engine3d.FamousLandmark.BIG_BEN_WESTMINSTER, uk)
    }
}
