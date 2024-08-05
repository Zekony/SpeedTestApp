package com.zekony.speed.ui.speedTestScreen.composables.testScreens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.zekony.speed.ooklaSpeedtest.models.TestType
import com.zekony.speed.ui.speedTestScreen.composables.SpeedProgressBar

@Composable
fun TestingScreen(
    testName: String,
    downloadSpeed: Float,
    testType: TestType
) {
    Text(text = testName, fontSize = 36.sp)
    SpeedProgressBar(
        currentValue = downloadSpeed,
        primaryColor = if (testType == TestType.DownloadTest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        secondaryColor = MaterialTheme.colorScheme.onBackground
    )
}