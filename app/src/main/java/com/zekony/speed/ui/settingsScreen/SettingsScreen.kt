package com.zekony.speed.ui.settingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekony.speed.R
import com.zekony.speed.data.datastore.AppThemeType
import com.zekony.speed.ooklaSpeedtest.models.TestType
import com.zekony.speed.ui.settingsScreen.viewModel.SettingsState

@Composable
fun SettingsScreen(
    state: SettingsState,
    onTestCheckBox: (TestType) -> Unit,
    chooseTheme: (AppThemeType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TestsCheckboxes(testsToRun = state.testsToRun, testTypeCheckBox = onTestCheckBox)
        HorizontalDivider()
        Text(
            text = stringResource(R.string.choose_theme),
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp
        )
        state.currentThemeType?.let {
            ThemeCheckBoxes(it, chooseTheme)
        }
    }
}

@Composable
fun ThemeCheckBoxes(currentTheme: AppThemeType, chooseTheme: (AppThemeType) -> Unit) {
    AppThemeType.values().forEach { theme ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { chooseTheme(theme) }) {
            Checkbox(
                checked = currentTheme == theme,
                onCheckedChange = { chooseTheme(theme) },
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = when (theme) {
                    AppThemeType.Dark -> stringResource(R.string.dark_theme)
                    AppThemeType.Light -> stringResource(R.string.light_theme)
                    AppThemeType.OC -> stringResource(R.string.oc_theme)
                }
            )
        }
    }
}


@Composable
fun TestsCheckboxes(
    testsToRun: Set<TestType>,
    testTypeCheckBox: (TestType) -> Unit
) {
    TestType.values().forEach { type ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { testTypeCheckBox(type) }) {
            Checkbox(
                checked = testsToRun.contains(type),
                onCheckedChange = { testTypeCheckBox(type) },
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = when (type) {
                    TestType.DownloadTest -> stringResource(R.string.test_download)
                    TestType.UploadTest -> stringResource(R.string.test_upload)
                }
            )
        }
    }
}