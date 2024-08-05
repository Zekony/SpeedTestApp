package com.zekony.speed.ui.speedTestScreen.viewModel

import com.zekony.speed.data.network.ApiStatus
import com.zekony.speed.ooklaSpeedtest.models.STServer
import com.zekony.speed.ooklaSpeedtest.models.TestType
import com.zekony.speed.ooklaSpeedtest.models.TestingStatus

data class SpeedTestState(
    val downloadSpeed: Float = 0f,
    val uploadSpeed: Float = 0f,
    val downloadSpeedList: List<Float> = emptyList(),
    val uploadSpeedList: List<Float> = emptyList(),
    val downloadStatus: ApiStatus = ApiStatus.Loading,
    val testingStatus: TestingStatus = TestingStatus.Idle,
    val testsToRun: Set<TestType> = setOf(),
    val selectedSTServer: STServer? = null,
    val serversList: List<STServer> = listOf()
)