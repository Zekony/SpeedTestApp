package com.zekony.speed.ui.speedTestScreen.composables.testScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekony.speed.R
import com.zekony.speed.ui.speedTestScreen.viewModel.SpeedTestState


@Composable
fun ChooseServerScreen(state: SpeedTestState, chooseServer: (String) -> Unit) {
    if (state.serversList.isNotEmpty()) {
        state.serversList.forEach { server ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { chooseServer(server.url ?: "") },
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(text = "${server.sponsor}, ${server.name}")
                    Text(
                        text = stringResource(R.string.url, server.url.toString()),
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            HorizontalDivider()
        }
    } else {
        CircularProgressIndicator()
        Text(text = stringResource(R.string.load_servers))
    }
}