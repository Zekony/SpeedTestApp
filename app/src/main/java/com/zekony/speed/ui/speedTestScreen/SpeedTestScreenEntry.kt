package com.zekony.speed.ui.speedTestScreen

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zekony.speed.ooklaSpeedtest.models.STServer
import com.zekony.speed.ooklaSpeedtest.models.TestingStatus
import com.zekony.speed.ui.speedTestScreen.viewModel.SpeedTestState
import com.zekony.speed.ui.speedTestScreen.viewModel.SpeedTestViewModel
import org.koin.androidx.compose.koinViewModel


const val SPEED_TEST_ROUTE = "speed test"

fun NavGraphBuilder.speedTestEntry() {

    composable(SPEED_TEST_ROUTE) {
        val viewModel: SpeedTestViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        SpeedTestScreen(
            state,
            viewModel::startDownloadTest,
            viewModel::restart,
            viewModel::chooseServer
        )
    }
}