package com.zekony.speed.ui.settingsScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zekony.speed.ui.settingsScreen.viewModel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

const val SETTINGS_ROUTE = "settings"

fun NavGraphBuilder.settingsScreenEntry() {

    composable(SETTINGS_ROUTE) {
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        SettingsScreen(state, viewModel::checkTestType, viewModel::changeTheme)
    }
}
