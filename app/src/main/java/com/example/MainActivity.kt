package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.dashboard.DailyDashboardScreen
import com.example.ui.engine3d.FamousLandmark
import com.example.ui.studio3d.WeatherStudioScreen
import com.example.ui.theme.SkySphereTheme
import com.example.ui.viewmodel.WeatherViewModel

enum class AppScreen {
    DASHBOARD,
    STUDIO_3D
}

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkySphereTheme {
                MainWeatherApp(viewModel = weatherViewModel)
            }
        }
    }
}

@Composable
fun MainWeatherApp(viewModel: WeatherViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    // Handle system back gesture
    if (currentScreen != AppScreen.DASHBOARD) {
        BackHandler {
            currentScreen = AppScreen.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0B1426).copy(alpha = 0.95f),
                contentColor = Color.White,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { currentScreen = AppScreen.DASHBOARD },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Forecast Dashboard"
                        )
                    },
                    label = { Text("Forecast") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF64B5F6),
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_dashboard")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.STUDIO_3D,
                    onClick = { currentScreen = AppScreen.STUDIO_3D },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "3D Weather Stage"
                        )
                    },
                    label = { Text("3D Stage") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF64B5F6),
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_item_3d_stage")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    DailyDashboardScreen(
                        viewModel = viewModel,
                        onNavigateTo3DStudio = { currentScreen = AppScreen.STUDIO_3D }
                    )
                }
                AppScreen.STUDIO_3D -> {
                    val currentCity by viewModel.selectedCity.collectAsState()
                    val landmark = FamousLandmark.forLocation(currentCity?.country, currentCity?.name)
                    WeatherStudioScreen(
                        currentCondition = viewModel.getCurrentCondition(),
                        initialLandmark = landmark,
                        onBackToDashboard = { currentScreen = AppScreen.DASHBOARD }
                    )
                }
            }
        }
    }
}
