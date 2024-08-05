package com.zekony.speed.ui.speedTestScreen.composables.testScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zekony.speed.R
import com.zekony.speed.ui.speedTestScreen.composables.TestCard
import com.zekony.speed.ui.speedTestScreen.viewModel.SpeedTestState

@Composable
fun FinishedTestScreen(state: SpeedTestState, restart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (state.downloadSpeedList.isNotEmpty()) {
            TestCard {
                Text(text = stringResource(R.string.download_speed))
                Text(
                    text = stringResource(
                        R.string.max_speed,
                        String.format("%.2f", state.downloadSpeedList.max())
                    )
                )
                Text(
                    text = stringResource(
                        R.string.mid_speed,
                        String.format("%.2f", state.downloadSpeedList.filter { it != 0f }.average())
                    )
                )
            }
        }
        if (state.uploadSpeedList.isNotEmpty()) {
            TestCard {
                Text(text = stringResource(R.string.upload_speed))
                Text(
                    text = stringResource(
                        R.string.max_speed,
                        String.format("%.2f", state.uploadSpeedList.max())
                    )
                )
                Text(
                    text = stringResource(
                        R.string.mid_speed,
                        String.format("%.2f", state.uploadSpeedList.filter { it != 0f }.average())
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(300.dp))
        Button(onClick = { restart() }, shape = RoundedCornerShape(4.dp)) {
            Text(text = stringResource(R.string.swap_server))
        }
    }
}