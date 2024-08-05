package com.zekony.speed.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.ui.graphics.vector.ImageVector
import com.zekony.speed.ui.settingsScreen.SETTINGS_ROUTE
import com.zekony.speed.ui.speedTestScreen.SPEED_TEST_ROUTE

enum class NavigationButtons(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    SpeedTest(
        SPEED_TEST_ROUTE,
        Icons.Filled.Speed,
        Icons.Outlined.Speed
    ),
    Settings(
        SETTINGS_ROUTE,
        Icons.Filled.Settings,
        Icons.Outlined.Settings
    ),
}