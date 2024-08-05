package com.zekony.speed.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zekony.speed.data.datastore.AppThemeType.Dark
import com.zekony.speed.data.datastore.AppThemeType.Light
import com.zekony.speed.data.datastore.AppThemeType.OC
import com.zekony.speed.ui.settingsScreen.settingsScreenEntry
import com.zekony.speed.ui.speedTestScreen.SPEED_TEST_ROUTE
import com.zekony.speed.ui.speedTestScreen.speedTestEntry
import com.zekony.speed.ui.theme.SpeedTestAppTheme
import org.koin.androidx.compose.koinViewModel


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: ThemeViewModel = koinViewModel()
    val stateTheme by viewModel.state.collectAsState()

    SpeedTestAppTheme(
        darkTheme = when (stateTheme) {
            Dark -> true
            Light -> false
            OC -> isSystemInDarkTheme()
        }
    ) {
        Scaffold(
            //containerColor = MaterialTheme.colorScheme.background,
            bottomBar = { AppBottomBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = SPEED_TEST_ROUTE,
                modifier = Modifier.padding(innerPadding)
            ) {
                speedTestEntry()
                settingsScreenEntry()
            }
        }
    }
}