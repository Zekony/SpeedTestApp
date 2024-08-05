package com.zekony.speed.ui.speedTestScreen.composables.testScreens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zekony.speed.R


@Composable
fun ErrorScreen(
    error: String,
    startTest: () -> Unit
) {
    Text(text = stringResource(R.string.error_message, error))
    Button(onClick = { startTest() }) {
        Text(text = stringResource(id = R.string.swap_server))
    }
}
