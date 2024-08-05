package com.zekony.speed.ui.speedTestScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zekony.speed.R
import com.zekony.speed.ooklaSpeedtest.models.TestType
import com.zekony.speed.ooklaSpeedtest.models.TestingStatus
import com.zekony.speed.ui.speedTestScreen.composables.testScreens.ChooseServerScreen
import com.zekony.speed.ui.speedTestScreen.composables.testScreens.ErrorScreen
import com.zekony.speed.ui.speedTestScreen.composables.testScreens.FinishedTestScreen
import com.zekony.speed.ui.speedTestScreen.composables.testScreens.IdleTestScreen
import com.zekony.speed.ui.speedTestScreen.composables.testScreens.TestingScreen
import com.zekony.speed.ui.speedTestScreen.viewModel.SpeedTestState

@Composable
fun SpeedTestScreen(
    state: SpeedTestState,
    startTest: () -> Unit,
    restart: () -> Unit,
    chooseServer: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        if (state.selectedSTServer == null) {
            ChooseServerScreen(state, chooseServer)
        } else {
            when (state.testingStatus) {
                is TestingStatus.Error -> {
                    ErrorScreen(state.testingStatus.error, restart)
                }

                is TestingStatus.Finished -> {
                    FinishedTestScreen(state, restart)
                }

                TestingStatus.Idle -> {
                    IdleTestScreen(
                        state.selectedSTServer,
                        startTest,
                        state.testsToRun
                    )
                }

                is TestingStatus.Testing -> {
                    if (state.testingStatus.testtype == TestType.UploadTest) {
                        TestingScreen(
                            stringResource(R.string.upload_speed),
                            state.uploadSpeed,
                            state.testingStatus.testtype
                        )
                    } else {
                        TestingScreen(
                            stringResource(R.string.download_speed),
                            state.downloadSpeed,
                            state.testingStatus.testtype
                        )
                    }
                }
            }
        }
    }
}



