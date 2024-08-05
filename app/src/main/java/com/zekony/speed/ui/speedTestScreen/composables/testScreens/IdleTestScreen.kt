package com.zekony.speed.ui.speedTestScreen.composables.testScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekony.speed.R
import com.zekony.speed.ooklaSpeedtest.models.STServer
import com.zekony.speed.ooklaSpeedtest.models.TestType
import com.zekony.speed.ui.speedTestScreen.composables.TestCard

@Composable
fun IdleTestScreen(
    server: STServer?,
    downloadTest: () -> Unit,
    testsToRun: Set<TestType>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TestCard {
            server?.let { server ->
                Text(
                    text = stringResource(R.string.sponsor, server.sponsor.toString()),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.server_name, server.name.toString()),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.server_url, server.url.toString()),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    Button(
        onClick = { downloadTest() },
        shape = RoundedCornerShape(4.dp),
        enabled = testsToRun.isNotEmpty(),
        modifier = Modifier.padding(12.dp)
    ) {
        Text(text = stringResource(R.string.start_test))
    }
}